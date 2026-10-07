import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { catalogService } from '../../services/catalogService';
import type { Book } from '../../types';
import './Catalog.css';

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
        <div className="catalog-container">
            <div className="search-bar-row">
                <input 
                    className="search-field" 
                    placeholder="Enter Book Name" 
                    value={name} 
                    onChange={e => setName(e.target.value)} 
                />
                <input 
                    className="search-field" 
                    placeholder="Enter Book Genre" 
                    value={genre} 
                    onChange={e => setGenre(e.target.value)} 
                />
                <input 
                    className="search-field" 
                    placeholder="Enter Free Text" 
                    value={freeText} 
                    onChange={e => setFreeText(e.target.value)} 
                />
                <button className="menu-button" onClick={handleSearch}>Search</button>
            </div>

            <table className="catalog-table">
                <thead>
                    <tr>
                        <th>Book Name</th>
                        <th>Book Genre</th>
                        <th>Keywords</th>
                        <th>Book Summary</th>
                    </tr>
                </thead>
                <tbody>
                    {books.map(book => (
                        <tr 
                            key={book.bookId} 
                            // This routes to the details page and passes the book data invisibly
                            onClick={() => navigate(`/book-details`, { state: { selectedBook: book } })}
                        >
                            <td>{book.title}</td>
                            <td>{book.genre}</td>
                            <td>{book.keywords}</td>
                            <td>{book.summary}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default SearchCatalog;