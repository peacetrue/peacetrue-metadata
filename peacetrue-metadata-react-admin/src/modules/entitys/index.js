import React from "react";
import {Resource} from "react-admin";

import {EntityList} from './list';
import {EntityCreate} from './create';
import {EntityEdit} from './edit';
import {EntityShow} from './show';

export const Entity = {list: EntityList, create: EntityCreate, edit: EntityEdit, show: EntityShow};
const EntityResource = <Resource name="entitys" {...Entity} />;
export default EntityResource;
