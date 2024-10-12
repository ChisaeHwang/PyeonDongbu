import React, { useState } from 'react';
import '../../styles/JobsPage.css';
import FilterButtonGroup from '../../components/FilterButtonGroup';
import RecruitmentPostList from '../../components/RecruitmentPostList';
import { FilterOptions } from '../../utils/FilterOptions';

const JobsPage = () => {
    const [selectedSkills, setSelectedSkills] = useState<string[]>([]);
    const [selectedVideoTypes, setSelectedVideoTypes] = useState<string[]>([]);
    const [selectedMaxSubs, setSelectedMaxSubs] = useState('');
    const [selectedPaymentType, setSelectedPaymentType] = useState('');
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

    const maxSubsOptions = [
        { value: '', label: '선택하세요' },
        { value: '0-1', label: '1만 이하' },
        { value: '1-5', label: '1~5만' },
        { value: '5-10', label: '5~10만' },
        { value: '10-50', label: '10~50만' },
        { value: '50-100', label: '50~100만' },
        { value: '100+', label: '100만 이상' },
    ];

    const paymentTypeOptions = [
        { value: '', label: '선택하세요' },
        { value: 'PER_HOUR', label: '분당' },
        { value: 'PER_PROJECT', label: '건당' },
        { value: 'MONTHLY_SALARY', label: '월급' },
        { value: 'NEGOTIABLE', label: '협의' },
    ];

    const workloadOptions = [
        { value: '', label: '선택하세요' },
        { value: '1-2', label: '주간 1~2개' },
        { value: '3-4', label: '주간 3~4개' },
        { value: '5+', label: '주간 5개 이상' },
    ];

    return (
        <div className="jobs-page">
            <div className="jobs-page-filter">
                <div className="jobs-page-header">
                    <h2 className="jobs-page-title">파트너를 찾고 계신 모든 유튜버 분들을 만나보세요!</h2>
                    <div className="jobs-page-filter-section">
                        <FilterButtonGroup
                            options={FilterOptions}
                            selectedOptions={[...selectedSkills, ...selectedVideoTypes]}
                            onOptionToggle={handleOptionToggle}
                        />
                    </div>
                </div>
                
                <div className="jobs-page-select-filters">
                    <div className="select-filter">
                        <label htmlFor="maxSubs">최대 구독자 수</label>
                        <select
                            id="maxSubs"
                            value={selectedMaxSubs}
                            onChange={(e) => setSelectedMaxSubs(e.target.value)}
                            className="details-select"
                        >
                            {maxSubsOptions.map(option => (
                                <option key={option.value} value={option.value}>{option.label}</option>
                            ))}
                        </select>
                    </div>
                    <div className="select-filter">
                        <label htmlFor="paymentType">급여 유형</label>
                        <select
                            id="paymentType"
                            value={selectedPaymentType}
                            onChange={(e) => setSelectedPaymentType(e.target.value)}
                            className="details-select"
                        >
                            {paymentTypeOptions.map(option => (
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

            <div className="jobs-page-post-list">
                <RecruitmentPostList
                    skills={selectedSkills}
                    videoTypes={selectedVideoTypes}
                    tagNames={['구인', '크롤링']}
                    maxSubs={selectedMaxSubs}
                    paymentType={selectedPaymentType}
                    workload={selectedWorkload}
                    variant="jobs"
                />
            </div>
        </div>
    );
};

export default JobsPage;