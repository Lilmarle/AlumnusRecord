const { createApp, ref, reactive } = Vue;

createApp({
    setup() {
        const loading = ref(false);
        const errorMsg = ref('');

        // 登录相关
        const loginForm = reactive({ account: '', password: '' });
        const loginSuccess = ref(false);
        const userInfo = ref(null);

        // ---------- 登录 ----------
        async function doLogin() {
            errorMsg.value = '';
            loginSuccess.value = false;
            userInfo.value = null;

            if (!loginForm.account.trim()) {
                errorMsg.value = '请输入用户名或手机号';
                return;
            }
            if (!loginForm.password.trim()) {
                errorMsg.value = '请输入密码';
                return;
            }

            loading.value = true;
            try {
                const response = await fetch('/user/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        account: loginForm.account,
                        password: loginForm.password
                    })
                });
                const result = await response.json();
                if (result.code === 1) {
                    // 保存到 localStorage
                    localStorage.setItem('userInfo', JSON.stringify(result.data));
                    loginSuccess.value = true;
                    userInfo.value = result.data;
                    // 登录成功后跳转到首页
                    setTimeout(() => {
                        window.location.href = '/home.html';
                    }, 500);
                } else {
                    errorMsg.value = result.message || '登录失败';
                }
            } catch (err) {
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        return {
            loading, errorMsg,
            loginForm, loginSuccess, userInfo, doLogin
        };
    }
}).mount('#app');
