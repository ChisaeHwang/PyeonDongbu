import Banner from '../components/Banner'
import RecruitmentPostList from '../components/RecruitmentPostList';
import '../styles/PostsPage.css';

const PostsPage = () => {
    return (
        <div className="posts-page">
            <Banner />
            <RecruitmentPostList
                tagNames={['크롤링']}
            />
        </div>
    );
};

export default PostsPage;