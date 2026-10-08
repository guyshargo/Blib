import React, { useEffect, useState } from 'react';

import { catalogService } from '../../services/catalogService';
import type { Book } from '../../types';
import BookCard from '../../components/BookCard';

const MemberHome: React.FC = () => {

    const [books, setBooks] = useState<Book[]>([]);
    
    useEffect(() => {
        const fetchCatalog = async () => {
            try {
                const results = await catalogService.searchBooks('is empty', 'is empty', 'is empty');
                setBooks(results);
            } catch (error) {
                console.error("Failed to fetch catalog", error);
            }
        };
        fetchCatalog();
    }, []);

    // slider categories
    const currentlyReading = books.slice(0, 2); 
    const topRated = books.filter((_, i) => i % 2 === 0);
    const latestArrivals = books.filter((_, i) => i % 2 !== 0);

    return (
        <div className="flex flex-col gap-[45px] py-[30px] px-[50px]">
            {/* Top Shelf: Currently Borrowed */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Currently reading</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto min-h-[220px]">
                    {currentlyReading.map(book => <BookCard key={book.bookId} book={book} />)}
                </div>
            </div>

            {/* Middle Shelf: Top Rated */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Top rated books</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto min-h-[220px]">
                    {topRated.map(book => <BookCard key={book.bookId} book={book} />)}
                </div>
            </div>

            {/* Bottom Shelf: Latest Arrivals */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Latest arrivals</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto min-h-[220px]">
                    {latestArrivals.map(book => <BookCard key={book.bookId} book={book} />)}
                </div>
            </div>
        </div>
    );
};

export default MemberHome;