var exec = require('cordova/exec');


var SKeystoreSDK = {
    coolMethod :function (message,index, success, error) {
        exec(success, error, 'SKeystoreSDK', 'coolMethod', [message,index]);
    }
}



module.exports = SKeystoreSDK;
