goog.provide('bjm.PageManager');

goog.require('goog.dom');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('goog.fx.dom.FadeInAndShow');


/**
 * @constructor
 * @export
 */
bjm.PageManager = function() {
};

/**
 * @export
 */
bjm.PageManager.prototype.initialize = function() {
  goog.events.listen(window,
      goog.events.EventType.LOAD,
      goog.bind(this.onDocumentLoaded_, this));
};

bjm.PageManager.prototype.onDocumentLoaded_ = function() {
  var div = goog.dom.getElement('main-div');
  div.innerText = 'This server and JS example are running splendidly!';
  var anim = new goog.fx.dom.FadeInAndShow(div, 5000);
  anim.play();
};
