import React from "react";
import {Resource} from "react-admin";

import {PropertyList} from './list';
import {PropertyCreate} from './create';
import {PropertyEdit} from './edit';
import {PropertyShow} from './show';

export const Property = {list: PropertyList, create: PropertyCreate, edit: PropertyEdit, show: PropertyShow};
const PropertyResource = <Resource name="propertys" {...Property} />;
export default PropertyResource;
