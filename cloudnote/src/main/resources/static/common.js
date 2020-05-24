//退出登录
function logout(token) {
    //清空localStorage
    localStorage.removeItem("token");
    localStorage.removeItem("headAccountName");
    localStorage.removeItem("headImageUrl");
    window.location.href = "/account/logout.json?token=" + token;
};


//笔记页面验证
function toNotePage(token) {
    window.location.href = "/to_note_page?token=" + token;
}

//图片页面验证
function toImagePage(token) {
    window.location.href = "/image_page?token=" + token;
}

//文件页面验证
function toFilePage(token) {
    window.location.href = "/file_page?token=" + token;
}

//日程页面验证
function toSchedulePage(token) {
    window.location.href = "/schedule_page?token=" + token;
}

//个人信息页面验证
function toInformationPage(token) {
    window.location.href = "/information?token=" + token;
}

//修改密码页面验证
function toResetPasswordPage(token) {
    window.location.href = "/resetPassword?token=" + token;
}

//回收站页面验证
function toRecycleBinPage(token) {
    window.location.href = "/recycle_bin?token=" + token;
}

//存储信息
function setLocalStorage(key, value) {
    //获取当前时间
    var curtime = new Date().getTime();
    var expireTime = curtime + 7 * 24 * 60 * 60 * 1000;//设置有效期为一周
    var valueDate = JSON.stringify({
        "expireTime": expireTime,
        "value": value
    });
    localStorage.setItem(key, valueDate);
}

//获取信息
function getLocalStorage(key) {
    var data = null;
    /**
     * 没有/过期都要重新登录
     */
    var vals = localStorage.getItem(key); // 获取本地存储的值
    if (vals != null) {
        var obj = JSON.parse(vals);
        if (obj.expireTime > new Date().getTime()) {
            data = obj.value;
        } else {
            localStorage.removeItem(key);
        }
    }
    return data;
}





