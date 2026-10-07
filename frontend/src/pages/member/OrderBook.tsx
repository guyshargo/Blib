import React, { useEffect, useState } from 'react';
import { orderService } from '../../services/orderService';
import type { OrderedBook, Book, Member } from '../../types';
import './Member.css';

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

    // Triggered when clicking a row in the table[cite: 43]
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
                fullName: member.fullName,
                phoneNum: member.phoneNum,
                email: member.email
            };

            const status = await orderService.orderBook(payload);
            
            if (status === "approve") {
                await orderService.addActivity({
                    memberId: member.id,
                    activityType: "order",
                    description: `order ${foundBook.title}`
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
                memberId: member.id,
                activityType: "cancelOrder",
                description: `cancel order ${cancelBook.title}`
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

    return (
        <div className="member-container">
            <h2 className="label-text" style={{ fontSize: '20px', alignSelf: 'center' }}>My Orders</h2>
            
            <table className="data-table">
                <thead>
                    <tr>
                        <th>BookID</th>
                        <th>Name</th>
                        <th>Arrived?</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map(order => (
                        <tr 
                            key={order.orderId} 
                            onClick={() => handleOrderClick(order)}
                            className={selectedOrder?.orderId === order.orderId ? 'selected' : ''}
                        >
                            <td>{order.bookId}</td>
                            <td>{order.title}</td>
                            <td>{order.arrivalStatus}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <div style={{ display: 'flex', gap: '15px', justifyContent: 'center' }}>
                <button className="menu-button" onClick={handleCancelOrder}>Cancel order</button>
                <button className="menu-button" onClick={handleReorder}>Re-order book</button>
            </div>

            <hr style={{ width: '100%', margin: '20px 0', borderTop: '1px solid #ccc' }} />

            <h2 className="label-text" style={{ fontSize: '30px', alignSelf: 'center' }}>Order a book now!</h2>
            
            <div style={{ display: 'flex', gap: '20px', justifyContent: 'center', alignItems: 'center' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <div className="info-group">
                        <span className="label-text" style={{ fontSize: '20px' }}>Book name:</span>
                        <input className="text-field" value={searchName} onChange={e => setSearchName(e.target.value)} />
                    </div>
                    <div className="info-group">
                        <span className="label-text" style={{ fontSize: '20px' }}>Book ID:</span>
                        <input className="text-field" value={searchId} onChange={e => setSearchId(e.target.value)} />
                    </div>
                </div>
                <button className="menu-button" onClick={handleSearchOrder}>Search book</button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: '20px' }}>
                <div className="info-group">
                    <span className="label-text" style={{ fontSize: '20px' }}>
                        {foundBook ? "Found book:" : ""}
                    </span>
                    <span style={{ fontSize: '20px' }}>{foundBook?.title}</span>
                </div>
                <button 
                    id="orderBtn" 
                    className="menu-button" 
                    style={{ marginTop: '15px' }}
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