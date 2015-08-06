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
name|j2ee
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_comment
comment|/**  * WebdavServlet provides webdav support (level 1 and 2 complient) for repository  * resources.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleWebdavServlet
extends|extends
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|webdav
operator|.
name|simple
operator|.
name|SimpleWebdavServlet
block|{
comment|/**      * the jcr repository      */
specifier|private
name|Repository
name|repository
decl_stmt|;
comment|/**      * Returns the<code>Repository</code>. If no repository has been set or      * created the repository initialized by<code>RepositoryAccessServlet</code>      * is returned.      *      * @return repository      * @see RepositoryAccessServlet#getRepository(ServletContext)      */
specifier|public
name|Repository
name|getRepository
parameter_list|()
block|{
if|if
condition|(
name|repository
operator|==
literal|null
condition|)
block|{
name|repository
operator|=
name|RepositoryAccessServlet
operator|.
name|getRepository
argument_list|(
name|getServletContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|repository
return|;
block|}
comment|/**      * Sets the<code>Repository</code>.      *      * @param repository      */
specifier|public
name|void
name|setRepository
parameter_list|(
name|Repository
name|repository
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
block|}
block|}
end_class

end_unit

