import React from 'react';
import {Datagrid, DateField, DateInput, EditButton, Filter, List, TextField, TextInput} from 'react-admin';

const Filters = (props) => (
    <Filter {...props}>
        <TextInput label={'编码'} source="code" allowEmpty alwaysOn resettable/>
        <TextInput label={'名称'} source="name" allowEmpty alwaysOn resettable/>
        <TextInput label={'备注'} source="remark" allowEmpty alwaysOn resettable/>
        <DateInput label={'创建时间起始值'} source="createdTime.lowerBound" allowEmpty alwaysOn/>
        <DateInput label={'创建时间结束值'} source="createdTime.upperBound" allowEmpty alwaysOn/>
        <DateInput label={'最近修改时间起始值'} source="modifiedTime.lowerBound" allowEmpty alwaysOn/>
        <DateInput label={'最近修改时间结束值'} source="modifiedTime.upperBound" allowEmpty alwaysOn/>
    </Filter>
);

export const PropertyList = props => {
    console.info('PropertyList:', props);
    return (
        <List {...props} filters={<Filters/>}>
            <Datagrid rowClick="show">
                <TextField source="entityId"/>
                <TextField source="code"/>
                <TextField source="name"/>
                <TextField source="typeId"/>
                <TextField source="referenceId"/>
                <TextField source="remark"/>
                <TextField source="serialNumber"/>
                <TextField source="creatorId"/>
                <DateField source="createdTime" showTime/>
                <TextField source="modifierId"/>
                <DateField source="modifiedTime" showTime/>
                <EditButton/>
            </Datagrid>
        </List>
    )
};
