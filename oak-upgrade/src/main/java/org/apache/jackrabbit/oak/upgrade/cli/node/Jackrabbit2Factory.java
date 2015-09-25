begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|upgrade
operator|.
name|cli
operator|.
name|node
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|IOUtils
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
name|lang
operator|.
name|StringUtils
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
name|core
operator|.
name|RepositoryContext
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
name|core
operator|.
name|config
operator|.
name|RepositoryConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Closer
import|;
end_import

begin_class
specifier|public
class|class
name|Jackrabbit2Factory
block|{
specifier|private
specifier|final
name|File
name|repositoryDir
decl_stmt|;
specifier|private
specifier|final
name|File
name|repositoryFile
decl_stmt|;
specifier|public
name|Jackrabbit2Factory
parameter_list|(
name|String
name|repositoryDir
parameter_list|,
name|String
name|repositoryFile
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isJcr2Repository
argument_list|(
name|repositoryDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Repository directory not found: "
operator|+
name|repositoryDir
argument_list|)
throw|;
block|}
name|this
operator|.
name|repositoryDir
operator|=
operator|new
name|File
argument_list|(
name|repositoryDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|repositoryFile
operator|=
operator|new
name|File
argument_list|(
name|repositoryFile
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRepositoryXml
argument_list|(
name|repositoryFile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Repository configuration not found: "
operator|+
name|repositoryFile
argument_list|)
throw|;
block|}
block|}
specifier|public
name|RepositoryContext
name|create
parameter_list|(
name|Closer
name|closer
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|RepositoryContext
name|source
init|=
name|RepositoryContext
operator|.
name|create
argument_list|(
name|RepositoryConfig
operator|.
name|create
argument_list|(
name|repositoryFile
argument_list|,
name|repositoryDir
argument_list|)
argument_list|)
decl_stmt|;
name|closer
operator|.
name|register
argument_list|(
name|asCloseable
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|source
return|;
block|}
specifier|public
name|File
name|getRepositoryDir
parameter_list|()
block|{
return|return
name|repositoryDir
return|;
block|}
specifier|private
specifier|static
name|Closeable
name|asCloseable
parameter_list|(
specifier|final
name|RepositoryContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|Closeable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|context
operator|.
name|getRepository
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isRepositoryXml
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|containsIgnoreCase
argument_list|(
name|line
argument_list|,
literal|"<Repository>"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isJcr2Repository
parameter_list|(
name|String
name|directory
parameter_list|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|File
name|workspaces
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"workspaces"
argument_list|)
decl_stmt|;
return|return
name|workspaces
operator|.
name|isDirectory
argument_list|()
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
literal|"JCR2[%s, %s]"
argument_list|,
name|repositoryDir
argument_list|,
name|repositoryFile
argument_list|)
return|;
block|}
block|}
end_class

end_unit

