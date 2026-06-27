const { createApp, ref, reactive } = Vue;

createApp({
    setup() {
        const loading = ref(false);
        const errorMsg = ref('');

        // 注册相关
        const registerForm = reactive({ username: '', password: '', phone: '', role: '' });
        const registerSuccess = ref(false);
        const registerResult = ref(null);

        // ---------- 注册 ----------
        async function doRegister() {
            errorMsg.value = '';
            registerSuccess.value = false;

            if (!registerForm.username.trim()) {
                errorMsg.value = '请输入用户名';
                return;
            }
            if (!registerForm.password.trim()) {
                errorMsg.value = '请输入密码';
                return;
            }
            if (!registerForm.phone.trim()) {
                errorMsg.value = '请输入手机号';
                return;
            }
            if (!registerForm.role) {
                errorMsg.value = '请选择角色';
                return;
            }

            loading.value = true;
            try {
                const response = await fetch('/user/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        username: registerForm.username,
                        password: registerForm.password,
                        phone: registerForm.phone,
                        role: Number(registerForm.role)
                    })
                });
                const result = await response.json();
                if (result.code === 1) {
                    registerSuccess.value = true;
                    registerResult.value = result.data;
                } else {
                    errorMsg.value = result.message || '注册失败';
                }
            } catch (err) {
                errorMsg.value = '网络错误，请稍后重试';
            } finally {
                loading.value = false;
            }
        }

        // 跳转到登录页
        function goToLogin() {
            window.location.href = '/index.html';
        }

        return {
            loading, errorMsg,
            registerForm, registerSuccess, registerResult, doRegister,
            goToLogin
        };
    }
}).mount('#app');
