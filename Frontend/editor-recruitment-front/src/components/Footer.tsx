import React from 'react';
import '../styles/Footer.css';

const Footer: React.FC = () => {
    return (
        <footer className="footer">
            <div className="footer-content">
                <div className="footer-section">
                    <h3>PDB</h3>
                    <p>비영리 프로젝트 매칭 플랫폼</p>
                </div>
                <div className="footer-section">
                    <h3>링크</h3>
                    <ul>
                        <li><a href="/about">소개</a></li>
                        <li><a href="/terms">이용약관</a></li>
                        <li><a href="/privacy">개인정보처리방침</a></li>
                    </ul>
                </div>
                <div className="footer-section">
                    <h3>문의</h3>
                    <p>이메일: contact@pdb.org</p>
                </div>
            </div>
            <div className="footer-bottom">
                <p>&copy; 2023 PDB. All rights reserved.</p>
            </div>
        </footer>
    );
};

export default Footer;