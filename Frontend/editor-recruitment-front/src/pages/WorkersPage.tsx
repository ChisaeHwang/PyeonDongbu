import React, { useState } from 'react';
import '../styles/WorkersPage.css';
import FilterButtonGroup from '../components/FilterButtonGroup';
import RecruitmentPostList from '../components/RecruitmentPostList';
import { FilterOptions } from '../utils/FilterOptions';

const WorkersPage = () => {
    const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
    const [selectedVideoTypes, setSelectedVideoTypes] = useState<string[]>([]);
    const [selectedSubscribers, setSelectedSubscribers] = useState('');
    const [selectedPayment, setSelectedPayment] = useState('');
    const [selectedWorkload, setSelectedWorkload] = useState('');

    const handleOptionToggle = (name: string, type: 'skill' | 'videoType') => {
        if (type === 'skill') {
            setSelectedSkills(prev => 
                prev.includes(name) ? prev.filter(skill => skill !== name) : [...prev, name]
            );
        } else {
            setSelectedVideoTypes(prev => 
                prev.includes(name) ? prev.filter(videoType => videoType !== name) : [...prev, name]
            );
        }
    };

    const subscriberOptions = [
        { value: '', label: '선택하세요' },
        { value: '10-50', label: '10~50만' },
        { value: '50-100', label: '50~100만' },
        { value: '100+', label: '100만+' },
    ];

    const paymentOptions = [
        { value: '', label: '선택하세요' },
        { value: '0-10', label: '분당 10만원 이하' },
        { value: '10-15', label: '분당 10~15만원' },
        { value: '15+', label: '분당 15만원 이상' },
    ];

    const workloadOptions = [
        { value: '', label: '선택하세요' },
        { value: '1-2', label: '주간 1~2개' },
        { value: '3-4', label: '주간 3~4개' },
        { value: '5+', label: '주간 5개 이상' },
    ];

    return (
        <div className="workers-page">
            <div className="workers-page-filter">
                <div className="workers-page-header">
                    <h2 className="workers-page-title">채널을 함께 키워나갈 파트너를 찾아보세요 !</h2>
                    <div className="workers-page-filter-section">
                        <FilterButtonGroup
                            options={FilterOptions}
                            selectedOptions={[...selectedSkills, ...selectedVideoTypes]}
                            onOptionToggle={handleOptionToggle}
                        />
                    </div>
                </div>
                
                <div className="workers-page-select-filters">
                    <div className="select-filter">
                        <label htmlFor="subscribers">구독자 수</label>
                        <select
                            id="subscribers"
                            value={selectedSubscribers}
                            onChange={(e) => setSelectedSubscribers(e.target.value)}
                            className="details-select"
                        >
                            {subscriberOptions.map(option => (
                                <option key={option.value} value={option.value}>{option.label}</option>
                            ))}
                        </select>
                    </div>
                    <div className="select-filter">
                        <label htmlFor="payment">페이</label>
                        <select
                            id="payment" 
                            value={selectedPayment}
                            onChange={(e) => setSelectedPayment(e.target.value)}
                            className="details-select"
                        >
                            {paymentOptions.map(option => (
                                <option key={option.value} value={option.value}>{option.label}</option>
                            ))}
                        </select>
                    </div>
                    <div className="select-filter">
                        <label htmlFor="workload">작업 갯수</label>
                        <select
                            id="workload"
                            value={selectedWorkload}
                            onChange={(e) => setSelectedWorkload(e.target.value)}
                            className="details-select"
                        >
                            {workloadOptions.map(option => (
                                <option key={option.value} value={option.value}>{option.label}</option>
                            ))}
                        </select>
                    </div>
                </div>
            </div>

            <div className="workers-page-post-list">
                <RecruitmentPostList
                    skills={selectedSkills}
                    videoTypes={selectedVideoTypes}
                    tagNames={['구직']}
                    variant="jobs"
                />
            </div>
        </div>
    );
};

export default WorkersPage;