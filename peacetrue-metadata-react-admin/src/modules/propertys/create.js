import React from 'react';
import {Create, maxLength, minValue, NumberInput, required, SimpleForm, TextInput} from 'react-admin';

export const PropertyCreate = (props) => {
    console.info('PropertyCreate:', props);
    return (
        <Create {...props}>
            <SimpleForm>
                <NumberInput source="entityId" validate={[required(), minValue(0)]} min={0}/>
                <TextInput source="code" validate={[required(), maxLength(32)]}/>
                <TextInput source="name" validate={[required(), maxLength(255)]}/>
                <NumberInput source="typeId" validate={[required(), minValue(0)]} min={0}/>
                <NumberInput source="referenceId" validate={[required(), minValue(0)]} min={0}/>
                <TextInput source="remark" validate={[required(), maxLength(255)]}/>
                <NumberInput source="serialNumber" validate={[required(), minValue(0)]} min={0}/>
            </SimpleForm>
        </Create>
    );
};
