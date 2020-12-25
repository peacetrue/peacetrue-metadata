import React from 'react';
import {DateField, Show, SimpleShowLayout, TextField} from 'react-admin';

export const EntityShow = (props) => {
    console.info('EntityShow:', props);
    return (
        <Show {...props}>
            <SimpleShowLayout>
                <TextField source="code"/>
                <TextField source="name"/>
                <TextField source="manyToMany"/>
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
