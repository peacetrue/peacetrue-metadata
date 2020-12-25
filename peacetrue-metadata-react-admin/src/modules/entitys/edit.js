import React from 'react';
import {Edit, maxLength, minValue, NumberInput, required, SimpleForm, TextInput} from 'react-admin';

export const EntityEdit = (props) => {
    console.info('EntityEdit:', props);
    return (
        <Edit {...props} undoable={false}>
            <SimpleForm>
                <TextInput source="code" validate={[required(), maxLength(64)]}/>
                <TextInput source="name" validate={[required(), maxLength(255)]}/>
                <TextInput source="manyToMany" validate={[required(), maxLength(1)]}/>
                <TextInput source="remark" validate={[required(), maxLength(255)]}/>
                <NumberInput source="serialNumber" validate={[required(), minValue(0)]} min={0}/>
            </SimpleForm>
        </Edit>
    );
};
