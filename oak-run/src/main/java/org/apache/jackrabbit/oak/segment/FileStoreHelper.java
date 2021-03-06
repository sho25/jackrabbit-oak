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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
specifier|public
class|class
name|FileStoreHelper
block|{
specifier|public
specifier|static
name|File
name|isValidFileStoreOrFail
parameter_list|(
name|File
name|store
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|isValidFileStore
argument_list|(
name|store
argument_list|)
argument_list|,
literal|"Invalid FileStore directory "
operator|+
name|store
argument_list|)
expr_stmt|;
return|return
name|store
return|;
block|}
comment|/**      * Checks if the provided directory is a valid FileStore      *      * @return true if the provided directory is a valid FileStore      */
specifier|public
specifier|static
name|boolean
name|isValidFileStore
parameter_list|(
name|File
name|store
parameter_list|)
block|{
if|if
condition|(
operator|!
name|store
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|store
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// for now the only check is the existence of the journal file
for|for
control|(
name|String
name|f
range|:
name|store
operator|.
name|list
argument_list|()
control|)
block|{
if|if
condition|(
literal|"journal.log"
operator|.
name|equals
argument_list|(
name|f
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

