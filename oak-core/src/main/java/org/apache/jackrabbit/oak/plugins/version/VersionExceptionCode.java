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
name|version
package|;
end_package

begin_comment
comment|/**  * {@code VersionExceptionCode} contains the codes for version related  * commit failures.  */
end_comment

begin_enum
specifier|public
enum|enum
name|VersionExceptionCode
block|{
name|UNEXPECTED_REPOSITORY_EXCEPTION
argument_list|(
literal|"Unexpected RepositoryException"
argument_list|)
block|,
name|NODE_CHECKED_IN
argument_list|(
literal|"Node is checked in"
argument_list|)
block|,
name|NO_SUCH_VERSION
argument_list|(
literal|"No such Version"
argument_list|)
block|,
name|OPV_ABORT_ITEM_PRESENT
argument_list|(
literal|"Item with OPV ABORT action present"
argument_list|)
block|,
name|NO_VERSION_TO_RESTORE
argument_list|(
literal|"No suitable version to restore"
argument_list|)
block|,
name|LABEL_EXISTS
argument_list|(
literal|"Version label already exists"
argument_list|)
block|,
name|NO_SUCH_VERSION_LABEL
argument_list|(
literal|"No such version label"
argument_list|)
block|,
name|ROOT_VERSION_REMOVAL
argument_list|(
literal|"Attempt to remove root version"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
name|VersionExceptionCode
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
block|}
end_enum

end_unit

