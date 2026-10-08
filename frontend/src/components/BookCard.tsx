import React from 'react';
import { useNavigate } from 'react-router-dom';
import type { Book } from '../types';

interface BookCardProps {
    book: Book;
}

const BookCard: React.FC<BookCardProps> = ({ book }) => {
    const navigate = useNavigate();

    // Safely fallback to the default logo if a specific ID cover hasn't been downloaded yet
    const handleImageError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
        e.currentTarget.src = '/images/book_logo.png';
    };

    return (
        <div 
            onClick={() => navigate(`/book-details`, { state: { selectedBook: book } })}
            className="flex flex-col items-center bg-transparent min-w-[120px] max-w-[120px] cursor-pointer transition-all duration-200 hover:-translate-y-2 group"
        >
            <img 
                src={`/images/covers/${book.bookId}.jpg`} 
                alt={book.title}
                onError={handleImageError}
                className="w-[120px] h-[180px] object-cover rounded-[2px] shadow-[3px_5px_10px_rgba(0,0,0,0.35)] transition-shadow duration-200 group-hover:shadow-[5px_8px_15px_rgba(0,0,0,0.5)]"
            />
            
            <div className="mt-2 font-sans font-extrabold text-[13px] text-[#3E3028] text-center w-full truncate px-1">
                {book.title}
            </div>
            
            {/* Using genre since author isn't in your Book interface yet */}
            <div className="font-sans text-[11px] text-[#8b6b58] text-center w-full truncate px-1">
                {book.genre}
            </div>
        </div>
    );
};

export default BookCard;