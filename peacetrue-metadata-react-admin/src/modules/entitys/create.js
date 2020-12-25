import React from 'react';
import {Create, maxLength, minValue, NumberInput, required, SimpleForm, TextInput} from 'react-admin';

export const EntityCreate = (props) => {
    console.info('EntityCreate:', props);
    return (
        <Create {...props}>
            <SimpleForm>
                <TextInput source="code" validate={[required(), maxLength(64)]}/>
                <TextInput source="name" validate={[required(), maxLength(255)]}/>
                <TextInput source="manyToMany" validate={[required(), maxLength(1)]}/>
                <TextInput source="remark" validate={[required(), maxLength(255)]}/>
                <NumberInput source="serialNumber" validate={[required(), minValue(0)]} min={0}/>
            </SimpleForm>
        </Create>
    );
};
