import React, { useEffect, useState } from 'react';
import { orderService } from '../../services/orderService';
import type { OrderedBook, Book, Member } from '../../types';

const OrderBook: React.FC = () => {
    const [member, setMember] = useState<Member | null>(null);
    const [orders, setOrders] = useState<OrderedBook[]>([]);
    
    // Selection state
    const [selectedOrder, setSelectedOrder] = useState<OrderedBook | null>(null);
    const [cancelBook, setCancelBook] = useState<Book | null>(null);

    // Search state
    const [searchName, setSearchName] = useState('');
    const [searchId, setSearchId] = useState('');
    const [foundBook, setFoundBook] = useState<Book | null>(null);

    useEffect(() => {
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            const member = JSON.parse(userStr);
            setMember(member);
            loadOrders(member.id);
        }
    }, []);

    const loadOrders = async (memberId: number) => {
        try {
            const data = await orderService.getMemberOrders(memberId);
            setOrders(data);
        } catch (e) {
            console.error(e);
        }
    };

    // Triggered when clicking a row in the table
    const handleOrderClick = async (order: OrderedBook) => {
        setSelectedOrder(order);
        try {
            const books = await orderService.searchToCancel(order.bookId);
            if (books && books.length > 0) setCancelBook(books[0]);
        } catch (error) {
            console.error(error);
        }
    };

    const handleSearchOrder = async () => {
        if (!searchName.trim() && !searchId.trim()) {
            alert("Must enter a criteria to search");
            setFoundBook(null);
            return;
        }

        try {
            const bName = searchName.trim() || 'null';
            const bId = searchId.trim() ? parseInt(searchId) : -1;
            
            const results = await orderService.searchToOrder(bName, bId);
            if (results && results.length > 0) {
                setFoundBook(results[0]);
            } else {
                setFoundBook(null);
                alert("No books found for the given search criteria");
            }
        } catch (error) {
            setFoundBook(null);
            alert("No books found for the given search criteria");
        }
    };

    const handleOrderBook = async () => {
        if (!foundBook || !member) return;
        
        if (member.freezeStatus === 'Frozen') {
            alert("Member is Frozen");
            return;
        }
        if (foundBook.copiesNum === foundBook.borrowedNum) {
            alert(`${foundBook.title} reached maximum ordering to the book`);
            return;
        }

        try {
            const payload = {
                title: foundBook.title,
                bookId: foundBook.bookId,
                memberId: member.id,
                memberName: member.fullName,
                memberPhone: member.phoneNum,
                memberEmail: member.email
            };

            const status = await orderService.orderBook(payload);
            
            if (status === "approve") {
                await orderService.addActivity({
                    membershipNumber: member.id,
                    activityType: "order",
                    entityId: foundBook.bookId
                });
                await orderService.updateOrderStatus(foundBook.bookId, foundBook.ordersNum, true);
                
                alert(`${foundBook.title} has been ordered`);
                setFoundBook(null);
                setSearchName('');
                setSearchId('');
                loadOrders(member.id);
            } else {
                alert(`Error in ordering ${foundBook.title} because of ${status}`);
            }
        } catch (error) {
            alert("Network error occurred.");
        }
    };

    const handleCancelOrder = async () => {
        if (!selectedOrder || !cancelBook || !member) {
            alert("Error: must select an order to cancel");
            return;
        }
        try {
            await orderService.cancelOrder(selectedOrder.orderId, selectedOrder.title, selectedOrder.arrivalStatus);
            
            await orderService.addActivity({
                membershipNumber: member.id,
                activityType: "cancelOrder",
                entityId: cancelBook.bookId
            });
            await orderService.updateOrderStatus(cancelBook.bookId, cancelBook.ordersNum, false);
            
            alert(`${selectedOrder.title} has been canceled from list`);
            setSelectedOrder(null);
            setCancelBook(null);
            loadOrders(member.id);
        } catch (error) {
            console.error(error);
        }
    };

    const handleReorder = async () => {
        if (!selectedOrder || !cancelBook) {
            alert("Error: must select an order to cancel");
            return;
        }
        setFoundBook(cancelBook);
        setCancelBook(null);
    };

    const inputClass = "rounded-[17px] text-sm px-[15px] py-2 border border-[#ccc] bg-[#f9f9f9]";
    const btnClass = "bg-[#fec999] rounded-full text-black text-base font-semibold border-2 border-transparent px-5 py-2 cursor-pointer transition-all duration-200 hover:bg-[#fda95c]/65 hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)]";

    return (
        <div className="flex flex-col p-[30px] font-sans min-h-screen gap-[30px]">
            <h2 className="text-[20px] font-bold text-[#333] self-center">My Orders</h2>
            
            <table className="w-full bg-[#fff8f2] rounded-[5px] border-collapse border-2 border-[#fec999] font-semibold">
                <thead>
                    <tr>
                        <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">BookID</th>
                        <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">Name</th>
                        <th className="bg-[#fec999] p-2.5 text-left border-r border-[#e5b488]">Arrived?</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr 
                            key={order.orderId} 
                            onClick={() => handleOrderClick(order)}
                            className={`cursor-pointer ${selectedOrder?.orderId === order.orderId ? 'bg-[#fec999] text-black' : 'odd:bg-[#fdf6f0] even:bg-[#ffffff] hover:bg-[#f4e2e1]'}`}
                        >
                            <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{order.bookId}</td>
                            <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{order.title}</td>
                            <td className="p-2.5 border-r border-[#f6e6d8] border-b border-[#f6e6d8]">{order.arrivalStatus}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div className="flex gap-[15px] justify-center">
                <button className={btnClass} onClick={handleCancelOrder}>Cancel order</button>
                <button className={btnClass} onClick={handleReorder}>Re-order book</button>
            </div>

            <hr className="w-full my-5 border-t border-[#ccc]" />

            <h2 className="text-[30px] font-bold text-[#333] self-center">Order a book now!</h2>
            
            <div className="flex gap-5 justify-center items-center">
                <div className="flex flex-col gap-[15px]">
                    <div className="flex items-center gap-2.5">
                        <span className="text-[20px] font-bold text-[#333]">Book name:</span>
                        <input className={inputClass} value={searchName} onChange={e => setSearchName(e.target.value)} />
                    </div>
                    <div className="flex items-center gap-2.5">
                        <span className="text-[20px] font-bold text-[#333]">Book ID:</span>
                        <input className={inputClass} value={searchId} onChange={e => setSearchId(e.target.value)} />
                    </div>
                </div>
                <button className={btnClass} onClick={handleSearchOrder}>Search book</button>
            </div>

            <div className="flex flex-col items-center mt-5">
                <div className="flex items-center gap-2.5">
                    <span className="text-[20px] font-bold text-[#333]">
                        {foundBook ? "Found book:" : ""}
                    </span>
                    <span className="text-[20px] text-[#333] font-semibold">{foundBook?.title}</span>
                </div>
                <button 
                    className="bg-[#50cc50] rounded-full text-white text-base font-semibold border-2 border-transparent px-5 py-2 transition-all duration-200 hover:bg-[#43b043] hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)] disabled:bg-[#a0dba0] disabled:cursor-not-allowed mt-[15px]"
                    onClick={handleOrderBook}
                    disabled={!foundBook}
                >
                    Order book
                </button>
            </div>
        </div>
    );
};

export default OrderBook;