import React from 'react';
import {DateField, Show, SimpleShowLayout, TextField} from 'react-admin';

export const PropertyShow = (props) => {
    console.info('PropertyShow:', props);
    return (
        <Show {...props}>
            <SimpleShowLayout>
                <TextField source="entityId"/>
                <TextField source="code"/>
                <TextField source="name"/>
                <TextField source="typeId"/>
                <TextField source="associateEntityId"/>
                <TextField source="remark"/>
                <TextField source="serialNumber"/>
                <TextField source="creatorId"/>
                <DateField source="createdTime" showTime/>
                <TextField source="modifierId"/>
                <DateField source="modifiedTime" showTime/>
            </SimpleShowLayout>
        </Show>
    );
};
