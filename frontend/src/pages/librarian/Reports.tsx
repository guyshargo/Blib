import React, { useEffect, useState } from 'react';
import { reportService } from '../../services/reportService';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './Librarian.css';

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
            
            // Simple frontend CSV parser to mirror report.parseCSV[cite: 63]
            const rows = report.data.trim().split('\n').slice(1); 
            const parsed = rows.map(row => row.split(','));
            
            setTableData(parsed);
        } catch (error) {
            alert("No data received or network error.");
        }
    };

    // Recharts expects array of objects
    const chartData = tableData.map(row => ({
        name: row[0] || 'Unknown', 
        value1: parseInt(row[1] || '0'), 
        value2: parseInt(row[2] || '0')
    }));

    return (
        <div className="librarian-container">
            <h2>System Reports</h2>
            <div className="search-header">
                <select className="combo-box" value={selectedMonth} onChange={e => setSelectedMonth(e.target.value)}>
                    <option value="">Select Month</option>
                    {availableDates.months.map(m => <option key={m} value={m}>{m}</option>)}
                </select>
                
                <select className="combo-box" value={selectedYear} onChange={e => setSelectedYear(e.target.value)}>
                    <option value="">Select Year</option>
                    {availableDates.years.map(y => <option key={y} value={y}>{y}</option>)}
                </select>

                <select className="combo-box" value={selectedType} onChange={e => setSelectedType(e.target.value)}>
                    <option value="Borrow Report">Borrowed Books Report</option>
                    <option value="Status Report">Members Status Report</option>
                </select>

                <button className="menu-button" onClick={handleGenerate}>Generate Report</button>
            </div>

            {tableData.length > 0 && (
                <div className="charts-container">
                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>Column 1</th>
                                <th>Column 2</th>
                                <th>Column 3</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tableData.map((row, idx) => (
                                <tr key={idx}>
                                    {row.map((cell: string, i: number) => <td key={i}>{cell}</td>)}
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div style={{ height: '300px', width: '100%', marginTop: '30px' }}>
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