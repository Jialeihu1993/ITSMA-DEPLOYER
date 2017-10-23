import {BASE_NAME} from 'constants/ServiceConfig';

export default function getImgPath(imgName) {
  return BASE_NAME + 'images/' + imgName;
}
