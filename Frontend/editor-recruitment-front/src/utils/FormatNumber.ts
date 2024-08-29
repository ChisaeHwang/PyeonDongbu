export function formatNumber(value: string): string {
    if (!value) return '';
    
    // 숫자만 추출
    const numbers = value.replace(/[^0-9]/g, '');
    
    // 3자리마다 쉼표 추가
    return numbers.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}