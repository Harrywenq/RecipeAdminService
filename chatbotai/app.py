import streamlit as st
from google import genai

genai_client = genai.Client(api_key="AIzaSyCz6zwYteyi0UoLJpoiTWsSP4qa0IgsLD0")

st.set_page_config(page_title="Hỏi đáp cùng Chat", layout="centered")
st.title("💬 Hỏi đáp cùng Chat")

user_input = st.text_input("Nhập câu hỏi của bạn:")

if user_input:
    with st.spinner("Đang gửi câu hỏi..."):
        try:
            response = genai_client.models.generate_content(
                model="gemini-2.0-flash",
                contents=user_input,
            )
            st.success("Phản hồi từ Chat:")
            st.write(response.text)
        except Exception as e:
            st.error(f"Đã xảy ra lỗi: {e}")
