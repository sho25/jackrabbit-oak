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
name|fixture
package|;
end_package

begin_import
import|import static
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
name|RepositoryConfigurationParser
operator|.
name|REPOSITORY_HOME_VARIABLE
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|RepositoryImpl
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
name|RepositoryConfigurationParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_class
specifier|public
class|class
name|JackrabbitRepositoryFixture
implements|implements
name|RepositoryFixture
block|{
specifier|private
specifier|final
name|int
name|bundleCacheSize
decl_stmt|;
specifier|private
name|RepositoryImpl
index|[]
name|cluster
decl_stmt|;
specifier|public
name|JackrabbitRepositoryFixture
parameter_list|(
name|int
name|bundleCacheSize
parameter_list|)
block|{
name|this
operator|.
name|bundleCacheSize
operator|=
name|bundleCacheSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|n
operator|==
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|Repository
index|[]
name|setUpCluster
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|n
operator|==
literal|1
condition|)
block|{
name|String
name|name
init|=
literal|"Jackrabbit-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Properties
name|variables
init|=
operator|new
name|Properties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
name|variables
operator|.
name|setProperty
argument_list|(
name|REPOSITORY_HOME_VARIABLE
argument_list|,
name|directory
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|variables
operator|.
name|setProperty
argument_list|(
literal|"bundleCacheSize"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|bundleCacheSize
argument_list|)
argument_list|)
expr_stmt|;
name|InputStream
name|xml
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"repository.xml"
argument_list|)
decl_stmt|;
name|RepositoryConfig
name|config
init|=
name|RepositoryConfig
operator|.
name|create
argument_list|(
operator|new
name|InputSource
argument_list|(
name|xml
argument_list|)
argument_list|,
name|variables
argument_list|)
decl_stmt|;
name|RepositoryImpl
name|repository
init|=
name|RepositoryImpl
operator|.
name|create
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|this
operator|.
name|cluster
operator|=
operator|new
name|RepositoryImpl
index|[]
block|{
name|repository
block|}
expr_stmt|;
return|return
operator|new
name|Repository
index|[]
block|{
name|repository
block|}
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|syncRepositoryCluster
parameter_list|(
name|Repository
modifier|...
name|nodes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|()
block|{
for|for
control|(
name|RepositoryImpl
name|repository
range|:
name|cluster
control|)
block|{
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|repository
operator|.
name|getConfig
argument_list|()
operator|.
name|getHomeDir
argument_list|()
argument_list|)
decl_stmt|;
name|repository
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Jackrabbit"
return|;
block|}
block|}
end_class

end_unit

