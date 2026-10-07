import React from 'react';
import './Member.css';

const MemberHome: React.FC = () => {
    
    // Placeholder rendering for the visual shelf effect
    const renderPlaceholderCards = (count: number) => {
        return Array.from({ length: count }).map((_, index) => (
            <div key={index} className="book-card-placeholder">
                {/* Later: <img src={book.coverUrl} /> */}
            </div>
        ));
    };

    return (
        <div className="shelf-container">
            {/* Top Shelf: Currently Borrowed */}
            <div>
                <div className="shelf-title">Currently reading</div>
                <div className="shelf-row">
                    {renderPlaceholderCards(2)}
                </div>
            </div>

            {/* Middle Shelf: Top Rated */}
            <div>
                <div className="shelf-title">Top rated books</div>
                <div className="shelf-row">
                    {renderPlaceholderCards(4)}
                </div>
            </div>

            {/* Bottom Shelf: Latest Arrivals */}
            <div>
                <div className="shelf-title">Latest arrivals</div>
                <div className="shelf-row">
                    {renderPlaceholderCards(3)}
                </div>
            </div>
        </div>
    );
};

export default MemberHome;