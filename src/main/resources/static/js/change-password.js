const { createApp, ref, reactive } = Vue;

createApp({
    setup() {
        const loading = ref(false);
        const errorMsg = ref('');

        // 修改密码表单
        const changeForm = reactive({ userId: '', oldPassword: '', newPassword: '' });
        const changeSuccess = ref(false);

        // ---------- 修改密码 ----------
        async function doChangePassword() {
            errorMsg.value = '';
            changeSuccess.value = false;

            if (!changeForm.userId) {
                errorMsg.value = '请输入用户ID';
                return;
            }
            if (!changeForm.oldPassword.trim()) {
                errorMsg.value = '请输入旧密码';
                return;
            }
            if (!changeForm.newPassword.trim()) {
                errorMsg.value = '请输入新密码';
                return;
            }
            if (changeForm.oldPassword === changeForm.newPassword) {
                errorMsg.value = '新密码不能与旧密码相同';
                return;
            }

            loading.value = true;
            try {
                const response = await fetch('/user/changePassword', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        userId: Number(changeForm.userId),
                        oldPassword: changeForm.oldPassword,
                        newPassword: changeForm.newPassword
                    })
                });
                const result = await response.json();
                if (result.code === 200) {
                    changeSuccess.value = true;
                } else {
                    errorMsg.value = result.message || '修改密码失败';
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
            changeForm, changeSuccess, doChangePassword,
            goToLogin
        };
    }
}).mount('#app');
