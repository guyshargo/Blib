import React, { useEffect, useState } from 'react';
import { reportService } from '../../services/reportService';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const Reports: React.FC = () => {
    const [availableDates, setAvailableDates] = useState<{ years: number[], months: number[] }>({ years: [], months: [] });
    
    const [selectedMonth, setSelectedMonth] = useState('');
    const [selectedYear, setSelectedYear] = useState('');
    const [selectedType, setSelectedType] = useState('Borrow Report');
    
    // Parsed CSV state arrays
    const [tableData, setTableData] = useState<any[]>([]);

    useEffect(() => {
        const fetchDates = async () => {
            try {
                const dates = await reportService.getAvailableDates();
                setAvailableDates(dates);
            } catch (error) {
                console.error("Failed to load dates");
            }
        };
        fetchDates();
    }, []);

    const handleGenerate = async () => {
        if (!selectedMonth || !selectedYear) {
            alert("Please select all fields.");
            return;
        }

        try {
            const reportTypeStr = selectedType === 'Borrow Report' ? 'borrow' : 'member_status';
            const report = await reportService.generateReport(reportTypeStr, parseInt(selectedMonth), selectedYear);
            
            // CSV parser
            const rows = report.data.trim().split('\n').slice(1); 
            const parsed = rows.map(row => row.split(','));
            
            setTableData(parsed);
        } catch (error) {
            alert("No data received or network error.");
        }
    };

    const chartData = tableData.map(row => ({
        name: row[0] || 'Unknown', 
        value1: parseInt(row[1] || '0'), 
        value2: parseInt(row[2] || '0')
    }));

    return (
        <div className="flex flex-col p-[30px] font-sans min-h-screen gap-5">
            <h2 className="text-2xl font-bold">System Reports</h2>
            <div className="flex items-end gap-5 bg-white p-5 rounded-[10px] shadow-[0_2px_10px_rgba(0,0,0,0.05)]">
                <select className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px]" value={selectedMonth} onChange={e => setSelectedMonth(e.target.value)}>
                    <option value="">Select Month</option>
                    {availableDates.months.map(m => <option key={m} value={m}>{m}</option>)}
                </select>
                
                <select className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px]" value={selectedYear} onChange={e => setSelectedYear(e.target.value)}>
                    <option value="">Select Year</option>
                    {availableDates.years.map(y => <option key={y} value={y}>{y}</option>)}
                </select>

                <select className="rounded-[5px] text-sm px-[15px] py-2.5 border border-[#ccc] outline-none min-w-[200px]" value={selectedType} onChange={e => setSelectedType(e.target.value)}>
                    <option value="Borrow Report">Borrowed Books Report</option>
                    <option value="Status Report">Members Status Report</option>
                </select>

                <button className="bg-[#fec999] rounded-full text-black text-base font-semibold border-2 border-transparent px-[25px] py-2.5 cursor-pointer transition-all duration-200 hover:bg-[#fda95c]/65 hover:shadow-[inset_0_3px_8px_rgba(0,0,0,0.3)]" onClick={handleGenerate}>
                    Generate Report
                </button>
            </div>

            {tableData.length > 0 && (
                <div className="flex flex-col gap-[30px] mt-5 bg-white p-5 rounded-[10px]">
                    <table className="w-full bg-[#fff8f2] rounded-[5px] border-collapse font-semibold mt-2.5">
                        <thead>
                            <tr>
                                <th className="bg-[#fec999] p-3 text-left">Column 1</th>
                                <th className="bg-[#fec999] p-3 text-left">Column 2</th>
                                <th className="bg-[#fec999] p-3 text-left">Column 3</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tableData.map((row, idx) => (
                                <tr key={idx} className="odd:bg-[#fdf6f0] even:bg-[#fee2cd] hover:bg-[#f4e2e1] hover:cursor-pointer">
                                    {row.map((cell: string, i: number) => <td key={i} className="p-3 border-b border-[#f6e6d8]">{cell}</td>)}
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className="h-[300px] w-full mt-[30px]">
                        <ResponsiveContainer>
                            <BarChart data={chartData}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="name" />
                                <YAxis />
                                <Tooltip />
                                <Legend />
                                <Bar dataKey="value1" fill="#8884d8" name="Metric 1" />
                                <Bar dataKey="value2" fill="#82ca9d" name="Metric 2" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Reports;