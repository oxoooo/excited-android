function gotoTop(){
    var timer = setInterval(function(){
        var toTop = document.body.scrollTop || document.documentElement.scrollTop;
        var speed = Math.ceil(toTop / 4);
        document.documentElement.scrollTop = document.body.scrollTop = toTop - speed;
        isTop = true;
        if (toTop == 0) {
            clearInterval(timer);
        }
    },30);
}
gotoTop();
