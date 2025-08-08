import React, {useState, useEffect} from 'react';
import './index.css';
import {Tooltip,Table, Button, Select, message, Popconfirm, Form, Spin, Modal, Input, Collapse, Tag} from 'antd';
import { useHistory } from 'ice';
import {
  queryInspectionCase,
  queryInspectionPicture,
  updateInspectionCasePrompt,
  insertInspectionCase,
  updateInspectionCase,
  deleteInspectionCase,
  retryValidation,
  queryInspectionPrompt,
} from '@api';
import debounce from 'lodash/debounce';
import { searchEmployee } from '@/pages/Operation/api/api';
import {checkFlagList, roleDescList, tagDescList} from "@common";

export default () => {
  const history = useHistory();
  const [InspectionQuery, setInspectionQuery] = useState<any>({
    workNo: '',
    tag: '',
    methodName: '',
    checkFlag: '',
    inspectionType: '',
  });
  const [tableDataList, setTableDataList] = useState<any>([]);
  const [employeeList, setEmployeeList] = useState<any>([]);
  const [tableLoading, setTableLoading] = useState<any>([]);
  const [formData, setFormData] = useState<any>({});
  const [modalLoading, setModalLoading] = useState<any>(false);
  const [retryLoading, setRetryLoading] = useState<any>(false);
  const [pictureLoading, setPictureLoading] = useState<any>({});
  const [updatePromptLoading, setUpdatePromptLoading] = useState<any>(false);
  const [isAdd, setIsAdd] = useState<any>(false);
  const [addFormRef] = Form.useForm();
  const [isEdit, setIsEdit] = useState<any>(false);
  const [editFormRef] = Form.useForm();
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number, range: [number, number]) => `共 ${total} 条记录，显示第 ${range[0]}-${range[1]} 条`,
  });

  useEffect(() => {
    queryTableList();
  }, []);


  const handleSearch = debounce((value: any) => {
    searchEmployee(value)
        .then((res) => {
          const data = res.map((item: any) => ({
            label: `${item.empName}-${item.workNo}`,
            value: item.workNo,
          }));
          setEmployeeList([...data]);
        })
        .catch((err) => {
          console.log(err.errorMsg, 'error');
        });
  }, 200);

  const loadPicture = async (id: string, picname: string) => {
    const loadingKey = `${id}_${picname}`;
    setPictureLoading(prev => ({ ...prev, [loadingKey]: true }));

    try {
      const pictureData = await queryInspectionPicture(id, picname);
      setPictureLoading(prev => ({ ...prev, [loadingKey]: false }));
      return pictureData;
    } catch (error) {
      setPictureLoading(prev => ({ ...prev, [loadingKey]: false }));
      message.error('图片加载失败，请重试');
      throw error;
    }
  };

  const onUpdatePrompt = async () => {
    // 检查必要参数
    if (!InspectionQuery.methodName) {
      message.warning('请先输入方法名进行筛选');
      return;
    }
    if (!InspectionQuery.inspectionType) {
      message.warning('请先选择巡检类型进行筛选');
      return;
    }

    setUpdatePromptLoading(true);
    try {
      const result = await updateInspectionCasePrompt(
          InspectionQuery.methodName,
          InspectionQuery.inspectionType,
          InspectionQuery.tag || 'public',
          InspectionQuery.checkFlag || 'ALL'
      );

      if (result) {
        message.success('Prompt更新成功');
        // 重新查询列表以获取最新数据
        queryTableList();
      } else {
        message.info('Prompt更新失败');
      }
    } catch (error) {
      console.error('更新Prompt失败:', error);
      message.error('更新Prompt失败，请稍后重试');
    } finally {
      setUpdatePromptLoading(false);
    }
  };


  const queryTableList = (pageIndex: number = pagination.current, pageSize: number = pagination.pageSize) => {
    setTableLoading(true);
    queryInspectionCase(
        InspectionQuery.tag ? InspectionQuery.tag : 'public',
        InspectionQuery.workNo ? InspectionQuery.workNo : '',
        InspectionQuery.methodName ? InspectionQuery.methodName : '',
        InspectionQuery.checkFlag ? InspectionQuery.checkFlag : 'ALL',
        InspectionQuery.inspectionType ? InspectionQuery.inspectionType : '',
        pageIndex,
        pageSize
    )
        .then((res) => {
          if (res != null && res.data && res.data.length > 0) {
            setTableDataList([...res.data]);
            setPagination({
              ...pagination,
              current: pageIndex,
              pageSize: pageSize,
              total: res.totalCount || 0,
            });
          } else {
            setTableDataList([]);
            setPagination({
              ...pagination,
              current: pageIndex,
              pageSize: pageSize,
              total: 0,
            });
          }
          setTableLoading(false);
        });
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '类型',
      dataIndex: 'tag',
      key: 'tag',
    },
    {
      title: '工号',
      dataIndex: 'workNo',
      key: 'workNo',
    },
    {
      title: '员工姓名',
      dataIndex: 'showName',
      key: 'showName',
    },
    {
      title: '方法名',
      dataIndex: 'methodName',
      key: 'methodName',
    },
    {
      title: '请求参数',
      dataIndex: 'req',
      key: 'req',
      render: (text: string, record: any) => {
        if (!text) return '';

        try {
          // 尝试解析 JSON
          const jsonData = JSON.parse(text);
          const thisPicName = jsonData.thisPicName;
          const lastPicName = jsonData.lastPicName;

          // 检查是否有图片数据需要显示，且巡检类型不是api
          const hasThisPic = thisPicName && record.inspectionType !== 'api';
          const hasLastPic = lastPicName && record.inspectionType !== 'api';

          if (hasThisPic || hasLastPic) {
            return (
                <div>
                  {hasThisPic && (
                      <div style={{ marginBottom: '4px' }}>
                        <span style={{ fontSize: '12px', color: '#666' }}>当前图片: </span>
                        <a
                            href="#"
                            onClick={async (e) => {
                              e.preventDefault();
                              const loadingKey = `${record.id}_${thisPicName}`;

                              try {
                                const pictureData = await loadPicture(record.id, thisPicName);

                                Modal.info({
                                  title: `当前图片 - ${thisPicName}`,
                                  width: 800,
                                  closable: true,
                                  content: (
                                      <div style={{ textAlign: 'center' }}>
                                        <img
                                            src={pictureData}
                                            alt={thisPicName}
                                            style={{
                                              maxWidth: '100%',
                                              maxHeight: '600px',
                                              objectFit: 'contain',
                                              border: '1px solid #d9d9d9',
                                              borderRadius: '4px'
                                            }}
                                        />
                                      </div>
                                  ),
                                  okText: '下载',
                                  onOk() {
                                    try {
                                      // 将 base64 转换为 Blob
                                      const base64Data = pictureData.split(',')[1];
                                      const byteCharacters = atob(base64Data);
                                      const byteNumbers = new Array(byteCharacters.length);
                                      for (let i = 0; i < byteCharacters.length; i++) {
                                        byteNumbers[i] = byteCharacters.charCodeAt(i);
                                      }
                                      const byteArray = new Uint8Array(byteNumbers);
                                      const blob = new Blob([byteArray], { type: 'image/png' });

                                      // 创建下载链接
                                      const url = URL.createObjectURL(blob);
                                      const link = document.createElement('a');
                                      link.href = url;
                                      link.download = thisPicName;
                                      document.body.appendChild(link);
                                      link.click();
                                      document.body.removeChild(link);

                                      // 清理 URL 对象
                                      setTimeout(() => URL.revokeObjectURL(url), 100);
                                      message.success('下载成功');
                                    } catch (error) {
                                      console.error('下载失败:', error);
                                      message.error('下载失败，请重试');
                                    }
                                  },
                                });
                              } catch (error) {
                                // 错误已在loadPicture中处理
                              }
                            }}
                            style={{
                              color: pictureLoading[`${record.id}_${thisPicName}`] ? '#999' : '#1890ff',
                              textDecoration: 'underline',
                              fontSize: '12px',
                              cursor: pictureLoading[`${record.id}_${thisPicName}`] ? 'wait' : 'pointer'
                            }}
                        >
                          {pictureLoading[`${record.id}_${thisPicName}`] ? '加载中...' : thisPicName}
                        </a>
                      </div>
                  )}
                  {hasLastPic && (
                      <div style={{ marginBottom: '4px' }}>
                        <span style={{ fontSize: '12px', color: '#666' }}>历史图片: </span>
                        <a
                            href="#"
                            onClick={async (e) => {
                              e.preventDefault();
                              const loadingKey = `${record.id}_${lastPicName}`;

                              try {
                                const pictureData = await loadPicture(record.id, lastPicName);

                                Modal.info({
                                  title: `历史图片 - ${lastPicName}`,
                                  width: 800,
                                  closable: true,
                                  content: (
                                      <div style={{ textAlign: 'center' }}>
                                        <img
                                            src={pictureData}
                                            alt={lastPicName}
                                            style={{
                                              maxWidth: '100%',
                                              maxHeight: '600px',
                                              objectFit: 'contain',
                                              border: '1px solid #d9d9d9',
                                              borderRadius: '4px'
                                            }}
                                        />
                                      </div>
                                  ),
                                  okText: '下载',
                                  onOk() {
                                    try {
                                      // 将 base64 转换为 Blob
                                      const base64Data = pictureData.split(',')[1];
                                      const byteCharacters = atob(base64Data);
                                      const byteNumbers = new Array(byteCharacters.length);
                                      for (let i = 0; i < byteCharacters.length; i++) {
                                        byteNumbers[i] = byteCharacters.charCodeAt(i);
                                      }
                                      const byteArray = new Uint8Array(byteNumbers);
                                      const blob = new Blob([byteArray], { type: 'image/png' });

                                      // 创建下载链接
                                      const url = URL.createObjectURL(blob);
                                      const link = document.createElement('a');
                                      link.href = url;
                                      link.download = lastPicName;
                                      document.body.appendChild(link);
                                      link.click();
                                      document.body.removeChild(link);

                                      // 清理 URL 对象
                                      setTimeout(() => URL.revokeObjectURL(url), 100);
                                      message.success('下载成功');
                                    } catch (error) {
                                      console.error('下载失败:', error);
                                      message.error('下载失败，请重试');
                                    }
                                  },
                                });
                              } catch (error) {
                                // 错误已在loadPicture中处理
                              }
                            }}
                            style={{
                              color: pictureLoading[`${record.id}_${lastPicName}`] ? '#999' : '#52c41a',
                              textDecoration: 'underline',
                              fontSize: '12px',
                              cursor: pictureLoading[`${record.id}_${lastPicName}`] ? 'wait' : 'pointer'
                            }}
                        >
                          {pictureLoading[`${record.id}_${lastPicName}`] ? '加载中...' : lastPicName}
                        </a>
                      </div>
                  )}
                  <div style={{ fontSize: '12px', color: '#999' }}>
                    {text.length > 30 ? `${text.substring(0, 30)}...` : text}
                  </div>
                </div>
            );
          }

          // 如果没有图片数据，按原来的方式显示
          const maxLength = 50;
          const displayText = text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;

          return (
              <Tooltip title={text} placement="topLeft">
                <span style={{ cursor: 'pointer' }}>{displayText}</span>
              </Tooltip>
          );
        } catch (error) {
          // 如果不是有效的 JSON，按原来的方式显示
          const maxLength = 50;
          const displayText = text.length > maxLength ? `${text.substring(0, maxLength)}...` : text;

          return (
              <Tooltip title={text} placement="topLeft">
                <span style={{ cursor: 'pointer' }}>{displayText}</span>
              </Tooltip>
          );
        }
      },
    },
    {
      title: 'Prompt内容',
      dataIndex: 'prompt',
      key: 'prompt',
      width: 300,
      render: (text: string, record: any) => {
        // 如果是接口模式，不显示Prompt内容
        if (record?.inspectionType === 'api') {
          return '-';
        }

        if (!text) return '-';

        return (
            <Tooltip title={text} placement="topLeft">
              <div
                  style={{
                    width: '280px',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                    cursor: 'pointer'
                  }}
              >
                {text}
              </div>
            </Tooltip>
        );
      },
    },
    {
      title: '最近一次操作时间',
      dataIndex: 'gmtModified',
      key: 'gmtModified',
    },
    {
      title: '执行结果',
      dataIndex: 'checkFlag',
      key: 'checkFlag',
      render: (_, record) => (
          <div>{checkFlagList[_]}</div>
      )
    },
    {
      title: '错误信息',
      dataIndex: 'errorMsg',
      key: 'errorMsg',
      render: (item, record) => {
        // 如果没有错误信息
        if (!item || (Array.isArray(item) && item.length === 0)) {
          return <span style={{ color: '#52c41a' }}>无错误</span>;
        }

        // 如果是字符串类型的JSON
        let errorData = item;
        if (typeof item === 'string') {
          try {
            errorData = JSON.parse(item);
          } catch (e) {
            errorData = item;
          }
        }

        // 如果解析后仍是字符串，直接显示在Collapse中
        if (typeof errorData === 'string') {
          return (
              <Collapse className='moduleList-collapse' ghost>
                <Collapse.Panel
                    header={record.checkFlag === 'N' ? "展开 / 收起配置" : "配置已锁定"}
                    key={record.id}
                    showArrow={record.checkFlag === 'N'}
                    disabled={record.checkFlag === 'Y' || record.checkFlag === 'U'}
                >
                  <div style={{ color: '#ff4d4f', backgroundColor: '#fff2f0', padding: '8px', borderRadius: '4px', border: '1px solid #ffccc7' }}>
                    {errorData}
                  </div>
                </Collapse.Panel>
              </Collapse>
          );
        }

        // 如果是数组，显示格式化的错误信息
        if (Array.isArray(errorData)) {
          return (
              <Collapse className='moduleList-collapse' ghost>
                <Collapse.Panel
                    header={record.checkFlag === 'N' ? "展开 / 收起配置" : "配置已锁定"}
                    key={record.id}
                    showArrow={record.checkFlag === 'N'}
                    disabled={record.checkFlag === 'Y' || record.checkFlag === 'U'}
                >
                  {errorData.map((res: any, index: number) => (
                      <div key={index} style={{marginBottom: 10}}>
                        <Tag color={'green'}>{res.indicatorCode}【{res.indicatorName}】</Tag>
                        <Tag color={'geekblue'}>【期望值】{res.expectedValue}【实际值】{res.actualValue}【理由】{res.reason}</Tag>
                      </div>
                  ))}
                </Collapse.Panel>
              </Collapse>
          );
        }

        // 其他情况，显示原始JSON
        return (
            <Collapse className='moduleList-collapse' ghost>
              <Collapse.Panel
                  header={record.checkFlag === 'N' ? "展开 / 收起配置" : "配置已锁定"}
                  key={record.id}
                  showArrow={record.checkFlag === 'N'}
                  disabled={record.checkFlag === 'Y' || record.checkFlag === 'U'}
              >
                <div style={{ color: '#ff4d4f', backgroundColor: '#fff2f0', padding: '8px', borderRadius: '4px', border: '1px solid #ffccc7' }}>
                  {JSON.stringify(errorData, null, 2)}
                </div>
              </Collapse.Panel>
            </Collapse>
        );
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record) => (
          <div>
            <Button
                size="small"
                type="link"
                style={{ fontSize: 12}}
                onClick={() => {
                  setIsEdit(true);
                  onEdit(record);
                }}
            >
              编辑
            </Button>
            <Button
                size="small"
                type="link"
                style={{ fontSize: 12, color: '#1890ff'}}
                onClick={() => onRetry(record)}
            >
              重试
            </Button>
            <Popconfirm
                title="删除为高危操作，是否确认删除？"
                onConfirm={() => onDelete(record)}
                okText="删除"
                cancelText="取消"
            >
              <Button size="small" type="link" style={{fontSize: 12, color: 'red'}}>删除</Button>
            </Popconfirm>
          </div>
      ),
    },
  ];

  const onDelete = (record: any) => {
    formData.id = record.id;
    setFormData({...formData});
    deleteInspectionCase(formData)
        .then((res: any) => {
          if (res) {
            message.success('删除成功');
          } else {
            message.info('删除失败');
          }
          onReset();
          setModalLoading(false);
          queryTableList();
        });
  };

  const onRetry = (record: any) => {
    setRetryLoading(true);
    retryValidation(record.id)
        .then((res: any) => {
          if (res) {
            message.success('重试成功');
          } else {
            message.info('重试失败');
          }
          queryTableList();
        })
        .catch((err) => {
          console.log(err);
          message.error('重试失败，请稍后重试');
        })
        .finally(() => {
          setRetryLoading(false);
        });
  };

  const onReset = () => {
    // 彻底清空 formData 中的所有字段
    setFormData({
      id: undefined,
      tag: undefined,
      workNo: undefined,
      methodName: undefined,
      req: undefined,
      inspectionType: undefined,
      prompt: undefined
    });
    setIsAdd(false);
    setIsEdit(false);
    addFormRef.resetFields();
    editFormRef.resetFields();
  };

  const onFinish = () => {
    // 如果是页面模式，将 prompt 的值填充到 req 字段
    if (formData.inspectionType === 'page' && formData.prompt) {
      formData.req = formData.prompt;
    }

    setFormData({ ...formData });
    insertInspectionCase(formData)
        .then((res: any) => {
          if (res) {
            message.success('创建成功');
          } else {
            message.info('创建失败');
          }
          onReset();
          setModalLoading(false);
          queryTableList();
        });
    onReset();
  };

  const onSubmitSearch = () => {
    setPagination({...pagination, current: 1});
    queryTableList(1, pagination.pageSize);
  };

  const onResetSearch = () => {
    InspectionQuery.workNo = '';
    InspectionQuery.tag = '';
    InspectionQuery.methodName = '';
    InspectionQuery.inspectionType = '';

    setPagination({...pagination, current: 1});
    queryTableList(1, pagination.pageSize);
    setInspectionQuery({...InspectionQuery});
  };

  const onEditFinish = () => {
    updateInspectionCase(formData)
        .then((res: any) => {
          if (res) {
            message.success('创建成功');
          } else {
            message.info('创建失败');
          }
          onReset();
          setModalLoading(false);
          queryTableList();
        });
  };

  // 查询Prompt配置并填充到Prompt字段
  const queryAndFillPrompt = async (tag: string, methodName: string) => {
    if (!tag || !methodName) {
      return;
    }

    try {
      const promptList = await queryInspectionPrompt(methodName, '', tag);
      if (promptList && promptList.length > 0) {
        // 取第一个匹配的配置
        const promptConfig = promptList[0];
        formData.prompt = promptConfig.prompt;
        setFormData({ ...formData });
        // 更新表单字段值
        if (isAdd) {
          addFormRef.setFieldsValue({ prompt: promptConfig.prompt });
        } else if (isEdit) {
          editFormRef.setFieldsValue({ prompt: promptConfig.prompt });
        }
        message.info('已自动填充Prompt配置内容');
      } else {
        // 如果没有找到匹配的配置，清空字段并提示
        formData.prompt = '';
        setFormData({ ...formData });
        if (isAdd) {
          addFormRef.setFieldsValue({ prompt: '' });
        } else if (isEdit) {
          editFormRef.setFieldsValue({ prompt: '' });
        }

      }
    } catch (error) {
      console.log('查询Prompt配置失败:', error);
      message.error('查询Prompt配置失败');
    }
  };

  const onEdit = (record: any) => {

    setIsEdit(true);
    formData.id = record.id;
    formData.tag = record.tag;
    formData.workNo = record.workNo;
    formData.methodName = record.methodName;
    formData.req = record.req;
    formData.inspectionType = record.inspectionType;
    formData.prompt = record.prompt;

    editFormRef.setFieldsValue({...formData})
    setModalLoading(false);
  };

  return (
      <Spin spinning={retryLoading || updatePromptLoading} tip={retryLoading ? "正在重试中，请稍后..." : "正在更新Prompt，请稍后..."}>
        <div className={'Inspection'}>

          <Modal
              title="新增"
              okText={'保存'}
              cancelText={'取消'}
              forceRender
              maskClosable={false}
              visible={isAdd}
              onOk={() => addFormRef.submit()}
              onCancel={onReset}
          >
            <Spin spinning={modalLoading}>
              <Form layout="vertical" form={addFormRef} onFinish={onFinish}>
                <Form.Item
                    label="单元类型"
                    rules={[{
                      required: true,
                      message: '经营单元类型不能为空',
                    }]}
                >
                  <Select
                      placeholder="请选择经营单元类型"
                      allowClear
                      value={formData.tag}
                      onChange={(tag: any) => {
                        formData.tag = tag;
                        setFormData({...formData});

                        // 如果是页面类型，且有方法名，则自动查询Prompt配置
                        if (formData.inspectionType === 'page' && formData.methodName && tag) {
                          queryAndFillPrompt(tag, formData.methodName);
                        }
                      }}
                  >
                    {Object.keys(tagDescList)?.map((key:any) => <Select.Option value={key}>{tagDescList[key]}</Select.Option>)}
                  </Select>
                </Form.Item>

                <Form.Item
                    label="巡检类型"
                    rules={[{
                      required: true,
                      message: '巡检类型不能为空',
                    }]}
                >
                  <Select
                      placeholder="请选择巡检类型"
                      allowClear
                      value={formData.inspectionType}
                      onChange={(inspectionType: any) => {
                        formData.inspectionType = inspectionType;
                        setFormData({...formData});

                        // 如果选择页面类型，且有单元类型和方法名，则自动查询Prompt配置
                        if (inspectionType === 'page' && formData.tag && formData.methodName) {
                          queryAndFillPrompt(formData.tag, formData.methodName);
                        }
                      }}
                  >
                    <Select.Option value="api">API</Select.Option>
                    <Select.Option value="page">页面</Select.Option>
                  </Select>
                </Form.Item>

                <Form.Item
                    name="workNo"
                    label="工号"
                    rules={[{
                      required: true,
                      message: '人员不能为空',
                    }]}
                >
                  <Select
                      showSearch
                      placeholder="请选择人员"
                      onSearch={handleSearch}
                      allowClear
                      value={formData.workNo}
                      onChange={(res: any) => {
                        formData.workNo = res;
                        setFormData({ ...formData });
                      }}
                  >
                    {employeeList?.map((res: any) => <Select.Option value={res.value}>{res.label}</Select.Option>)}
                  </Select>
                </Form.Item>

                <Form.Item
                    name="方法名"
                    label="方法名"
                    rules={[{
                      required: true,
                      message: '方法名不能为空',
                    }]}
                >
                  <Input
                      placeholder="请输入方法名"
                      maxLength={50}
                      allowClear
                      value={formData.methodName}
                      onChange={(e) => {
                        formData.methodName = e.target.value;
                        setFormData({ ...formData });

                        // 如果是页面类型，且有单元类型，则自动查询Prompt配置
                        if (formData.inspectionType === 'page' && formData.tag && e.target.value) {
                          queryAndFillPrompt(formData.tag, e.target.value);
                        }
                      }}
                  />
                </Form.Item>

                {formData.inspectionType === 'api' && (
                    <Form.Item
                        name="req"
                        label="请求参数"
                        rules={[{
                          required: true,
                          message: '请求参数不能为空',
                        }]}
                    >
                      <Input
                          placeholder="请输入请求参数"
                          allowClear
                          value={formData.req}
                          onChange={(e) => {
                            formData.req = e.target.value;
                            setFormData({ ...formData });
                          }}
                      />
                    </Form.Item>
                )}

                {formData.inspectionType === 'page' && (
                    <Form.Item
                        name="prompt"
                        label="Prompt内容"
                        rules={[{
                          required: true,
                          message: 'Prompt内容不能为空',
                        }]}
                    >
                      <Input.TextArea
                          placeholder="请输入Prompt内容"
                          allowClear
                          value={formData.prompt}
                          rows={4}
                          onChange={(e) => {
                            formData.prompt = e.target.value;
                            setFormData({ ...formData });
                          }}
                      />
                    </Form.Item>
                )}
              </Form>
            </Spin>
          </Modal>

          <Modal
              title="编辑"
              okText={'保存'}
              cancelText={'取消'}
              forceRender
              maskClosable={false}
              open={isEdit}
              onOk={() => editFormRef.submit()}
              onCancel={onReset}
          >
            <Spin spinning={modalLoading}>

              <Form layout="vertical" form={editFormRef} onFinish={onEditFinish}>
                <Form.Item
                    label="单元类型"
                    rules={[{
                      required: true,
                      message: '经营单元类型不能为空',
                    }]}
                >
                  <Select
                      placeholder="请选择经营单元类型"
                      allowClear
                      value={formData.tag}
                      onChange={(tag: any) => {
                        formData.tag = tag;
                        setFormData({...formData});

                        // 如果是页面类型，且有方法名，则自动查询Prompt配置
                        if (formData.inspectionType === 'page' && formData.methodName && tag) {
                          queryAndFillPrompt(tag, formData.methodName);
                        }
                      }}
                  >
                    {Object.keys(tagDescList)?.map((key:any) => <Select.Option value={key}>{tagDescList[key]}</Select.Option>)}
                  </Select>
                </Form.Item>

                <Form.Item
                    label="巡检类型"
                    rules={[{
                      required: true,
                      message: '巡检类型不能为空',
                    }]}
                >
                  <Select
                      placeholder="请选择巡检类型"
                      allowClear
                      value={formData.inspectionType}
                      onChange={(inspectionType: any) => {
                        formData.inspectionType = inspectionType;
                        setFormData({...formData});

                        // 如果选择页面类型，且有单元类型和方法名，则自动查询Prompt配置
                        if (inspectionType === 'page' && formData.tag && formData.methodName) {
                          queryAndFillPrompt(formData.tag, formData.methodName);
                        }
                      }}
                  >
                    <Select.Option value="api">接口</Select.Option>
                    <Select.Option value="page">页面</Select.Option>
                  </Select>
                </Form.Item>

                <Form.Item
                    name="workNo"
                    label="工号"
                    rules={[{
                      required: true,
                      message: '人员不能为空',
                    }]}
                >
                  <Select
                      showSearch
                      placeholder="请选择人员"
                      onSearch={handleSearch}
                      allowClear
                      value={formData.workNo}
                      onChange={(res: any) => {
                        formData.workNo = res;
                        setFormData({ ...formData });
                      }}
                  >
                    {employeeList?.map((res: any) => <Select.Option value={res.value}>{res.label}</Select.Option>)}
                  </Select>
                </Form.Item>

                <Form.Item
                    name="methodName"
                    label="方法名"
                    rules={[{
                      required: true,
                      message: '方法名不能为空',
                    }]}
                >
                  <Input
                      placeholder="请输入方法名"
                      value={formData.methodName}
                      allowClear
                      onChange={(e) => {
                        formData.methodName = e.target.value;
                        setFormData({ ...formData });

                        // 如果是页面类型，且有单元类型，则自动查询Prompt配置
                        if (formData.inspectionType === 'page' && formData.tag && e.target.value) {
                          queryAndFillPrompt(formData.tag, e.target.value);
                        }
                      }}
                  />
                </Form.Item>
                <Form.Item
                    name="req"
                    label="请求参数"
                    rules={[{
                      required: true,
                      message: '请求参数不能为空',
                    }]}
                >
                  <Input
                      placeholder="请输入请求参数"
                      allowClear
                      value={formData.req}
                      disabled={formData.inspectionType === 'page'}
                      onChange={(e) => {
                        formData.req = e.target.value;
                        setFormData({ ...formData });
                      }}
                  />
                </Form.Item>

                {formData.inspectionType === 'page' && (
                    <Form.Item
                        name="prompt"
                        label="Prompt内容"
                        rules={[{
                          required: true,
                          message: 'Prompt内容不能为空',
                        }]}
                    >
                      <Input.TextArea
                          placeholder="请输入Prompt内容"
                          allowClear
                          value={formData.prompt}
                          rows={4}
                          onChange={(e) => {
                            formData.prompt = e.target.value;
                            setFormData({ ...formData });
                          }}
                      />
                    </Form.Item>
                )}
              </Form>
            </Spin>
          </Modal>

          <div
              className="Inspection-header"
              style={{
                display: 'flex',
                margin: '15px 20px',
              }}
          >

            <div style={{
              marginRight: 20,
              width: 240,
            }}
            >
              <label style={{
                padding: 5,
                display: 'block',
                fontWeight: 500,
              }}
              >
                工号：
              </label>
              <Select
                  showSearch
                  placeholder="请选择人员"
                  onSearch={handleSearch}
                  allowClear
                  style={{width: 240}}
                  onChange={(res: any) => {
                    InspectionQuery.workNo = res;
                    setInspectionQuery({...InspectionQuery});
                  }}
              >
                {employeeList?.map((res: any) => <Select.Option value={res.value}>{res.label}</Select.Option>)}
              </Select>
            </div>

            <div style={{marginRight: 20}}>
              <label style={{
                padding: 5,
                display: 'block',
                fontWeight: 500,
              }}
              >
                类型：
              </label>
              <Select
                  defaultValue="public"
                  allowClear
                  placeholder="选择类型"
                  style={{width: 150}}
                  onChange={(tag: string) => {
                    InspectionQuery.tag = tag;
                    setInspectionQuery({...InspectionQuery});
                  }}
              >
                {Object.keys(tagDescList)?.map((key:any) => <Select.Option value={key}>{tagDescList[key]}</Select.Option>)}
              </Select>
            </div>

            <div style={{marginRight: 20}}>
              <label style={{
                padding: 5,
                display: 'block',
                fontWeight: 500,
              }}
              >
                方法名：
              </label>
              <Input
                  placeholder="请输入方法名"
                  allowClear
                  style={{width: 200}}
                  value={InspectionQuery.methodName}
                  onChange={(e) => {
                    InspectionQuery.methodName = e.target.value;
                    setInspectionQuery({...InspectionQuery});
                  }}
              />
            </div>

            <div style={{marginRight: 20}}>
              <label style={{
                padding: 5,
                display: 'block',
                fontWeight: 500,
              }}
              >
                巡检类型：
              </label>
              <Select
                  allowClear
                  placeholder="选择巡检类型"
                  style={{width: 300}}
                  onChange={(inspectionType: string) => {
                    InspectionQuery.inspectionType = inspectionType;
                    setInspectionQuery({...InspectionQuery});
                  }}
              >
                <Select.Option value="api">接口</Select.Option>
                <Select.Option value="page">页面</Select.Option>
              </Select>
            </div>

            <div style={{marginRight: 20}}>
              <label style={{
                padding: 5,
                display: 'block',
                fontWeight: 500,
              }}
              >
                执行结果：
              </label>
              <Select
                  defaultValue="ALL"
                  allowClear
                  placeholder="选择执行结果"
                  style={{width: 300}}
                  onChange={(checkFlag: string) => {
                    InspectionQuery.checkFlag = checkFlag;
                    setInspectionQuery({...InspectionQuery});
                  }}
              >
                <Select.Option value="ALL">所有</Select.Option>
                <Select.Option value="U">未执行</Select.Option>
                <Select.Option value="N">未通过</Select.Option>
                <Select.Option value="Y">通过</Select.Option>
              </Select>
            </div>


            <div style={{marginTop: 30}}>
              <Button style={{marginRight: 10}} onClick={onSubmitSearch} type="primary">
                查询
              </Button>
              <Button style={{marginRight: 10}} onClick={onResetSearch}>
                重置
              </Button>
              <Button
                  style={{marginRight: 10}}
                  onClick={onUpdatePrompt}
                  loading={updatePromptLoading}
                  type="default"
              >
                更新Prompt
              </Button>
            </div>
          </div>

          <div style={{
            padding: '10px 0',
            margin: '20px 20px 5px',
            borderTop: '1px solid #f5f3f3',
          }}
          >
            <Button style={{ marginRight: 10 }} onClick={() => setIsAdd(true)} size="middle" type="primary">
              新增数据
            </Button>
          </div>
          <div style={{padding: '0 20px'}}>
            <Table
                loading={tableLoading}
                columns={columns}
                dataSource={tableDataList}
                size="middle"
                pagination={{
                  ...pagination,
                  onChange: (page, pageSize) => {
                    queryTableList(page, pageSize);
                  },
                  onShowSizeChange: (current, size) => {
                    queryTableList(1, size);
                  },
                }}
            />
          </div>
        </div>
      </Spin>
  );
};
