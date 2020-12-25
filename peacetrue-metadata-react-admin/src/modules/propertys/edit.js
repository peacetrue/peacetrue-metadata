import React from 'react';
import {Edit, maxLength, minValue, NumberInput, required, SimpleForm, TextInput} from 'react-admin';

export const PropertyEdit = (props) => {
    console.info('PropertyEdit:', props);
    return (
        <Edit {...props} undoable={false}>
            <SimpleForm>
                <NumberInput source="entityId" validate={[required(), minValue(0)]} min={0}/>
                <TextInput source="code" validate={[required(), maxLength(32)]}/>
                <TextInput source="name" validate={[required(), maxLength(255)]}/>
                <NumberInput source="typeId" validate={[required(), minValue(0)]} min={0}/>
                <NumberInput source="associateEntityId" validate={[required(), minValue(0)]} min={0}/>
                <TextInput source="remark" validate={[required(), maxLength(255)]}/>
                <NumberInput source="serialNumber" validate={[required(), minValue(0)]} min={0}/>
            </SimpleForm>
        </Edit>
    );
};
