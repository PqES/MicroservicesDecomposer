/**
 * 
 */
// You should import the CSS file.
// import 'viewerjs/dist/viewer.css';
import Viewer from 'viewerjs';

// View an image
const viewer = new Viewer(document.getElementById('image'), {
  inline: true,
  viewed() {
    viewer.zoomTo(1);
  },
});
// Then, show the image by click it, or call `viewer.show()`.

// View a list of images
/* const gallery = new Viewer(document.getElementById('images')); */
// Then, show one image by click it, or call `gallery.show()`.