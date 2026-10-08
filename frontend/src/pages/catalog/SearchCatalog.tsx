import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { catalogService } from '../../services/catalogService';
import type { Book } from '../../types';

const SearchCatalog: React.FC = () => {
    const [name, setName] = useState('');
    const [genre, setGenre] = useState('');
    const [freeText, setFreeText] = useState('');
    const [books, setBooks] = useState<Book[]>([]);
    
    const navigate = useNavigate();

    const handleSearch = async () => {
        try {
            const results = await catalogService.searchBooks(name, genre, freeText);
            
            if (results && results.length > 0) {
                setBooks(results);
            } else {
                setBooks([]);
                alert("No books match the search criteria.");
            }
        } catch (error) {
            alert("Network Error: Could not connect to server.");
        }
    };

    return (
        <div className="flex flex-col items-center p-[30px] font-sans min-h-screen">
            <div className="flex gap-[15px] mb-5">
                <input 
                    className="rounded-[17px] text-sm px-[15px] py-2 border border-[#ccc] font-semibold" 
                    placeholder="Enter Book Name" 
                    value={name} 
                    onChange={e => setName(e.target.value)} 
                />
                <input 
                    className="rounded-[17px] text-sm px-[15px] py-2 border border-[#ccc] font-semibold" 
                    placeholder="Enter Book Genre" 
                    value={genre} 
                    onChange={e => setGenre(e.target.value)} 
                />
                <input 
                    className="rounded-[17px] text-sm px-[15px] py-2 border border-[#ccc] font-semibold" 
                    placeholder="Enter Free Text" 
                    value={freeText} 
                    onChange={e => setFreeText(e.target.value)} 
                />
                <button 
                    className="bg-[#fec999] rounded-full text-black text-lg font-semibold border-2 border-transparent px-[25px] py-2 transition-all duration-200 hover:bg-[#fda95c]/65 hover:text-[#333] hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)]" 
                    onClick={handleSearch}
                >
                    Search
                </button>
            </div>

            <table className="w-full max-w-[900px] bg-[#fff8f2] rounded border-collapse font-semibold">
                <thead>
                    <tr>
                        <th className="p-3 text-left border-b border-[#fdf6f0]">Book Name</th>
                        <th className="p-3 text-left border-b border-[#fdf6f0]">Book Genre</th>
                        <th className="p-3 text-left border-b border-[#fdf6f0]">Keywords</th>
                        <th className="p-3 text-left border-b border-[#fdf6f0]">Book Summary</th>
                    </tr>
                </thead>
                <tbody>
                    {books.map(book => (
                        <tr 
                            key={book.bookId} 
                            className="odd:bg-[#fdf6f0] even:bg-[#fee2cd] hover:bg-[#f4e2e1] hover:cursor-pointer"
                            onClick={() => navigate(`/book-details`, { state: { selectedBook: book } })}
                        >
                            <td className="p-3 text-left border-b border-[#fdf6f0]">{book.title}</td>
                            <td className="p-3 text-left border-b border-[#fdf6f0]">{book.genre}</td>
                            <td className="p-3 text-left border-b border-[#fdf6f0]">{book.keywords}</td>
                            <td className="p-3 text-left border-b border-[#fdf6f0]">{book.summary}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default SearchCatalog;