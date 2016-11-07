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
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_class
specifier|public
class|class
name|DirectoryUtils
block|{
comment|/**      * Get the file length in best effort basis.      * @return actual fileLength. -1 if cannot determine      */
specifier|public
specifier|static
name|long
name|getFileLength
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
try|try
block|{
comment|//Check for file presence otherwise internally it results in
comment|//an exception to be created
if|if
condition|(
name|dir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
return|return
name|dir
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{          }
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

