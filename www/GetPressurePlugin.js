/*
	See:
	http://www.apache.org/licenses/LICENSE-2.0
	for license information.
*/

var GetPressurePlugin = {
	getPressure:function(win, fail) {

		var pressureSuccess = function(result) {
			win(result);
		};

		var pressureFail = function(error) {
			fail(error);
		}

		cordova.exec(pressureSuccess, pressureFail, "GetPressure", "dummyAction", ["dummyArg"]);
	}
};

module.exports = GetPressurePlugin;