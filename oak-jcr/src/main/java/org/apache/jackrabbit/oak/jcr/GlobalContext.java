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
name|jcr
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
name|oak
operator|.
name|api
operator|.
name|ContentRepository
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
name|core
operator|.
name|KernelContentRepository
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
name|jcr
operator|.
name|util
operator|.
name|Unchecked
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
name|jcr
operator|.
name|RepositoryException
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

begin_import
import|import static
name|java
operator|.
name|text
operator|.
name|MessageFormat
operator|.
name|format
import|;
end_import

begin_comment
comment|/**  * Poor man's dependency injection  * todo: OAK-17: replace by some more sophisticated mechanism  * This class implements a poor man's dependency injection mechanism.  * It should be replaced by a more sophisticated mechanism for compile  * time dependency injection mechanism.  */
end_comment

begin_class
specifier|public
class|class
name|GlobalContext
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|instances
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|GlobalContext
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|put
argument_list|(
name|ContentRepository
operator|.
name|class
argument_list|,
operator|new
name|KernelContentRepository
argument_list|(
name|mk
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|Repository
operator|.
name|class
argument_list|,
operator|new
name|RepositoryImpl
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|forClass
parameter_list|)
block|{
name|T
name|instance
init|=
name|Unchecked
operator|.
expr|<
name|T
operator|>
name|cast
argument_list|(
name|instances
operator|.
name|get
argument_list|(
name|forClass
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|format
argument_list|(
literal|"Global context does not contain {0}"
argument_list|,
name|forClass
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|instance
return|;
block|}
comment|//------------------------------------------< private>---
specifier|private
parameter_list|<
name|T
parameter_list|,
name|I
extends|extends
name|T
parameter_list|>
name|void
name|put
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|classType
parameter_list|,
name|I
name|instance
parameter_list|)
block|{
if|if
condition|(
name|instances
operator|.
name|containsKey
argument_list|(
name|classType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|format
argument_list|(
literal|"Global context already contains {0}"
argument_list|,
name|classType
argument_list|)
argument_list|)
throw|;
block|}
name|instances
operator|.
name|put
argument_list|(
name|classType
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

