import React from 'react';
import '../styles/Modal.css';

interface ModalProps {
    children: React.ReactNode;
    onClose: () => void;
}

const Modal: React.FC<ModalProps> = ({ children, onClose }) => {
    return (
        <div className="post-editor-modal-overlay" onClick={onClose}>
            <div className="post-editor-modal-content" onClick={(e) => e.stopPropagation()}>
                <button className="post-editor-modal-close" onClick={onClose}>&times;</button>
                {children}
            </div>
        </div>
    );
};

export default Modal;