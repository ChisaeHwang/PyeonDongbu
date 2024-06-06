import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const renewAccessToken = async () => {
  const token = sessionStorage.getItem("access-token");
  const response = await axios.post(
    `${API_BASE_URL}/token`,
    {},
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return response.data.data.accessToken;
};
