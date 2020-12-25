export const entityMessages = {
    resources: {
        "entitys": {
            name: '实体',
            fields: {
                'id': '主键',
                'code': '编码',
                'name': '名称',
                'manyToMany': '多对多关联',
                'remark': '备注',
                'serialNumber': '序号',
                'creatorId': '创建者主键',
                'createdTime': '创建时间',
                'modifierId': '修改者主键',
                'modifiedTime': '最近修改时间',
            },
        }
    }
}

//['id','code','name','manyToMany','remark','serialNumber','creatorId','createdTime','modifierId','modifiedTime',]
//['主键','编码','名称','多对多关联','备注','序号','创建者主键','创建时间','修改者主键','最近修改时间',]

export default entityMessages;
