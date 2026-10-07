import React from 'react';

const MemberHome: React.FC = () => {
    
    // Placeholder rendering for the visual shelf effect
    const renderPlaceholderCards = (count: number) => {
        return Array.from({ length: count }).map((_, index) => (
            <div 
                key={index} 
                className="bg-[#D3C4B1] min-w-[120px] min-h-[175px] rounded-[2px] shadow-[3px_5px_10px_rgba(0,0,0,0.2)] cursor-pointer transition-all duration-200 hover:-translate-y-2 hover:shadow-[5px_8px_15px_rgba(0,0,0,0.3)]"
            >
                {/* Later: <img src={book.coverUrl} className="w-full h-full object-cover" /> */}
            </div>
        ));
    };

    return (
        <div className="flex flex-col gap-[45px] py-[30px] px-[50px]">
            {/* Top Shelf: Currently Borrowed */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Currently reading</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto">
                    {renderPlaceholderCards(2)}
                </div>
            </div>

            {/* Middle Shelf: Top Rated */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Top rated books</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto">
                    {renderPlaceholderCards(4)}
                </div>
            </div>

            {/* Bottom Shelf: Latest Arrivals */}
            <div>
                <div className="font-sans font-extrabold text-[22px] text-[#B83A24] mb-2.5">Latest arrivals</div>
                <div className="flex gap-[35px] pb-2.5 pl-5 border-b-[10px] border-[#CBA471] rounded-[3px] shadow-[0_5px_5px_rgba(0,0,0,0.1)] overflow-x-auto">
                    {renderPlaceholderCards(3)}
                </div>
            </div>
        </div>
    );
};

export default MemberHome;