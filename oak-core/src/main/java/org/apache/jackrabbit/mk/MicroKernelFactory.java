begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
package|;
end_package

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
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|client
operator|.
name|Client
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
name|mk
operator|.
name|fs
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
name|mk
operator|.
name|index
operator|.
name|IndexWrapper
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
name|mk
operator|.
name|simple
operator|.
name|SimpleKernelImpl
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
name|mk
operator|.
name|util
operator|.
name|ExceptionFactory
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
name|mk
operator|.
name|wrapper
operator|.
name|LogWrapper
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
name|mk
operator|.
name|wrapper
operator|.
name|SecurityWrapper
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
name|mk
operator|.
name|wrapper
operator|.
name|VirtualRepositoryWrapper
import|;
end_import

begin_comment
comment|/**  * A factory to create a MicroKernel instance.  */
end_comment

begin_class
specifier|public
class|class
name|MicroKernelFactory
block|{
comment|/**      * Get an instance. Supported URLs:      *<ul>      *<li>fs:target/mk-test (using the directory ./target/mk-test)</li>      *<li>fs:target/mk-test;clean (same, but delete the old repository first)</li>      *<li>fs:{homeDir} (use the system property homeDir or '.' if not set)</li>      *<li>simple: (in-memory implementation)</li>      *<li>simple:fs:target/temp (using the directory ./target/temp)</li>      *</ul>      *      * @param url the repository URL      * @return a new instance      */
specifier|public
specifier|static
name|MicroKernel
name|getInstance
parameter_list|(
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"mem:"
argument_list|)
condition|)
block|{
return|return
name|SimpleKernelImpl
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"simple:"
argument_list|)
condition|)
block|{
return|return
name|SimpleKernelImpl
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"log:"
argument_list|)
condition|)
block|{
return|return
name|LogWrapper
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"sec:"
argument_list|)
condition|)
block|{
return|return
name|SecurityWrapper
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"virtual:"
argument_list|)
condition|)
block|{
return|return
name|VirtualRepositoryWrapper
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"index:"
argument_list|)
condition|)
block|{
return|return
name|IndexWrapper
operator|.
name|get
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"fs:"
argument_list|)
condition|)
block|{
name|boolean
name|clean
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|endsWith
argument_list|(
literal|";clean"
argument_list|)
condition|)
block|{
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|url
operator|.
name|length
argument_list|()
operator|-
literal|";clean"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|clean
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|dir
init|=
name|url
operator|.
name|substring
argument_list|(
literal|"fs:"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|=
name|dir
operator|.
name|replaceAll
argument_list|(
literal|"\\{homeDir\\}"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"homeDir"
argument_list|,
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|clean
condition|)
block|{
try|try
block|{
name|FileUtils
operator|.
name|deleteRecursive
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
literal|".mk"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|MicroKernelImpl
argument_list|(
name|dir
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"http:"
argument_list|)
condition|)
block|{
return|return
name|Client
operator|.
name|createHttpClient
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"http-bridge:"
argument_list|)
condition|)
block|{
name|MicroKernel
name|mk
init|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|url
operator|.
name|substring
argument_list|(
literal|"http-bridge:"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Client
operator|.
name|createHttpBridge
argument_list|(
name|mk
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|url
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

