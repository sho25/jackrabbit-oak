begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|azure
operator|.
name|Configuration
operator|.
name|PID
import|;
end_import

begin_annotation_defn
annotation|@
name|ObjectClassDefinition
argument_list|(
name|pid
operator|=
block|{
name|PID
block|}
argument_list|,
name|name
operator|=
literal|"Apache Jackrabbit Oak Azure Segment Store Service"
argument_list|,
name|description
operator|=
literal|"Azure backend for the Oak Segment Node Store"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
name|String
name|PID
init|=
literal|"org.apache.jackrabbit.oak.segment.azure.AzureSegmentStoreService"
decl_stmt|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Azure account name"
argument_list|,
name|description
operator|=
literal|"Name of the Azure Storage account to use."
argument_list|)
name|String
name|accountName
parameter_list|()
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Azure container name"
argument_list|,
name|description
operator|=
literal|"Name of the container to use. If it doesn't exists, it'll be created."
argument_list|)
name|String
name|containerName
parameter_list|()
default|default
name|AzureSegmentStoreService
operator|.
name|DEFAULT_CONTAINER_NAME
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Azure account access key"
argument_list|,
name|description
operator|=
literal|"Access key which should be used to authenticate on the account"
argument_list|)
name|String
name|accessKey
parameter_list|()
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Root path"
argument_list|,
name|description
operator|=
literal|"Names of all the created blobs will be prefixed with this path"
argument_list|)
name|String
name|rootPath
parameter_list|()
default|default
name|AzureSegmentStoreService
operator|.
name|DEFAULT_ROOT_PATH
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Azure connection URL (optional)"
argument_list|,
name|description
operator|=
literal|"Connection URL to be used to connect to the Azure Storage. "
operator|+
literal|"Setting it will override the accountName, containerName and accessKey properties."
argument_list|)
name|String
name|connectionURL
parameter_list|()
default|default
literal|""
function_decl|;
block|}
end_annotation_defn

end_unit

