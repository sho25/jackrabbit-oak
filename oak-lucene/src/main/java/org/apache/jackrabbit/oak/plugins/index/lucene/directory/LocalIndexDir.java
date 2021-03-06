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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

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
name|checkState
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|IndexRootDirectory
operator|.
name|INDEX_METADATA_FILE_NAME
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|LocalIndexDir
implements|implements
name|Comparable
argument_list|<
name|LocalIndexDir
argument_list|>
block|{
specifier|final
name|File
name|dir
decl_stmt|;
specifier|final
name|IndexMeta
name|indexMeta
decl_stmt|;
specifier|public
name|LocalIndexDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dir
operator|=
name|dir
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
name|File
name|indexDetails
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|IndexRootDirectory
operator|.
name|INDEX_METADATA_FILE_NAME
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|isIndexDir
argument_list|(
name|dir
argument_list|)
argument_list|,
literal|"No file [%s] found in dir [%s]"
argument_list|,
name|INDEX_METADATA_FILE_NAME
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexMeta
operator|=
operator|new
name|IndexMeta
argument_list|(
name|indexDetails
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|FileUtils
operator|.
name|sizeOfDirectory
argument_list|(
name|dir
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
name|String
index|[]
name|listing
init|=
name|dir
operator|.
name|list
argument_list|()
decl_stmt|;
comment|//If some IO error occurs listing would be null
comment|//In such a case better to return false
if|if
condition|(
name|listing
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|//If the dir only has the meta file then it would be
comment|//considered as empty
return|return
name|listing
operator|.
name|length
operator|==
literal|1
return|;
block|}
specifier|public
name|String
name|getJcrPath
parameter_list|()
block|{
return|return
name|indexMeta
operator|.
name|indexPath
return|;
block|}
specifier|public
name|String
name|getFSPath
parameter_list|()
block|{
return|return
name|dir
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|LocalIndexDir
name|o
parameter_list|)
block|{
return|return
name|indexMeta
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|indexMeta
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%s)"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|indexMeta
argument_list|)
return|;
block|}
specifier|static
name|boolean
name|isIndexDir
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|File
name|indexDetails
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|IndexRootDirectory
operator|.
name|INDEX_METADATA_FILE_NAME
argument_list|)
decl_stmt|;
return|return
name|indexDetails
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
end_class

end_unit

