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
name|api
operator|.
name|MicroKernelException
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
name|core
operator|.
name|MicroKernelImpl
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
name|server
operator|.
name|Server
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SimpleKernelImpl
argument_list|>
name|INSTANCES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SimpleKernelImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|MicroKernelFactory
parameter_list|()
block|{     }
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
name|int
name|colon
init|=
name|url
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown repository URL: "
operator|+
name|url
argument_list|)
throw|;
block|}
name|String
name|head
init|=
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
decl_stmt|;
name|String
name|tail
init|=
name|url
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"mem"
argument_list|)
operator|||
name|head
operator|.
name|equals
argument_list|(
literal|"simple"
argument_list|)
operator|||
name|head
operator|.
name|equals
argument_list|(
literal|"fs"
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
name|tail
operator|.
name|endsWith
argument_list|(
literal|";clean"
argument_list|)
condition|)
block|{
name|tail
operator|=
name|tail
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|tail
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
name|tail
operator|=
name|tail
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
name|String
name|dir
decl_stmt|;
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"fs"
argument_list|)
condition|)
block|{
name|dir
operator|=
name|tail
operator|+
literal|"/.mk"
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|=
name|tail
operator|.
name|substring
argument_list|(
name|tail
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|INSTANCES
operator|.
name|remove
argument_list|(
name|tail
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|FileUtils
operator|.
name|deleteRecursive
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"fs"
argument_list|)
condition|)
block|{
return|return
operator|new
name|MicroKernelImpl
argument_list|(
name|tail
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|String
name|name
init|=
name|tail
decl_stmt|;
synchronized|synchronized
init|(
name|INSTANCES
init|)
block|{
name|SimpleKernelImpl
name|instance
init|=
name|INSTANCES
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|SimpleKernelImpl
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|dispose
parameter_list|()
block|{
name|super
operator|.
name|dispose
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|INSTANCES
init|)
block|{
name|INSTANCES
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|INSTANCES
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"log"
argument_list|)
condition|)
block|{
return|return
operator|new
name|LogWrapper
argument_list|(
name|getInstance
argument_list|(
name|tail
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"sec"
argument_list|)
condition|)
block|{
name|String
name|userPassUrl
init|=
name|tail
decl_stmt|;
name|int
name|index
init|=
name|userPassUrl
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Expected url format: sec:user@pass:<url>"
argument_list|)
throw|;
block|}
name|String
name|u
init|=
name|userPassUrl
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|userPass
init|=
name|userPassUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|index
operator|=
name|userPass
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Expected url format: sec:user@pass:<url>"
argument_list|)
throw|;
block|}
name|String
name|user
init|=
name|userPass
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
name|pass
init|=
name|userPass
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|MicroKernel
name|mk
init|=
name|getInstance
argument_list|(
name|u
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|SecurityWrapper
argument_list|(
name|mk
argument_list|,
name|user
argument_list|,
name|pass
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|super
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"virtual"
argument_list|)
condition|)
block|{
name|MicroKernel
name|mk
init|=
name|getInstance
argument_list|(
name|tail
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|VirtualRepositoryWrapper
argument_list|(
name|mk
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
return|return
operator|new
name|IndexWrapper
argument_list|(
name|getInstance
argument_list|(
name|tail
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"http"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Client
argument_list|(
name|url
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|head
operator|.
name|equals
argument_list|(
literal|"http-bridge"
argument_list|)
condition|)
block|{
name|MicroKernel
name|mk
init|=
name|getInstance
argument_list|(
name|tail
argument_list|)
decl_stmt|;
specifier|final
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
name|mk
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|Client
argument_list|(
name|server
operator|.
name|getAddress
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|dispose
parameter_list|()
block|{
name|super
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
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

