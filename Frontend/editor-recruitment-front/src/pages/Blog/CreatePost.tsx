import React, { useState } from "react";
import QuillEditor from "../../components/common/QuillEditor";
import axios from "axios";

const CreatePost: React.FC = () => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [submitStatus, setSubmitStatus] = useState<null | string>(null);

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  const handleContentChange = (content: string) => {
    setContent(content);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8080/api/recruitment/posts", {
        title,
        content,
      });
      if (response.status === 200) {
        setSubmitStatus("게시글이 성공적으로 제출되었습니다.");
      }
    } catch (error) {
      setSubmitStatus("게시글 제출에 실패했습니다.");
    }
  };

  return (
    <div>
      <h1>Create Post</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="title">Title</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={handleTitleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="content">Content</label>
          <QuillEditor value={content} onChange={handleContentChange} />
        </div>
        <button type="submit">Submit</button>
      </form>
      {submitStatus && <p>{submitStatus}</p>}
    </div>
  );
};

export default CreatePost;
