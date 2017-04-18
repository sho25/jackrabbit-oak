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
name|plugins
operator|.
name|blob
operator|.
name|migration
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|jmx
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|jmx
operator|.
name|Name
import|;
end_import

begin_interface
specifier|public
interface|interface
name|BlobMigrationMBean
block|{
name|String
name|TYPE
init|=
literal|"BlobMigration"
decl_stmt|;
annotation|@
name|Nonnull
annotation|@
name|Description
argument_list|(
literal|"Start or resume the blob migration"
argument_list|)
name|String
name|startBlobMigration
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"resume"
argument_list|)
annotation|@
name|Description
argument_list|(
literal|"true to resume stopped migration or false to start it from scratch"
argument_list|)
name|boolean
name|resume
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
annotation|@
name|Description
argument_list|(
literal|"Stop the blob migration"
argument_list|)
name|String
name|stopBlobMigration
parameter_list|()
function_decl|;
annotation|@
name|Nonnull
name|CompositeData
name|getBlobMigrationStatus
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
block|}
end_interface

end_unit

