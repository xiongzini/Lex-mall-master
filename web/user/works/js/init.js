// 对所有移动端页面进行基本的初始化
function setRemFontSize() {
    var sUserAgent = navigator.userAgent.toLowerCase();
    if (/ipad|iphone|midp|rv:1.2.3.4|ucweb|android|windows ce|windows mobile/.test(sUserAgent)) {
        //使用移动端大小
        let doc = document.documentElement
        doc.style.fontSize = doc.clientWidth / 375 * 20 + 'px';
    }
}


export {setRemFontSize}