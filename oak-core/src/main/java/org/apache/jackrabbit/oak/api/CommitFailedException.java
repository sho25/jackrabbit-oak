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
name|api
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ReferentialIntegrityException
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
name|javax
operator|.
name|jcr
operator|.
name|lock
operator|.
name|LockException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NoSuchNodeTypeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionException
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|format
import|;
end_import

begin_comment
comment|/**  * Main exception thrown by methods defined on the {@code ContentSession}  * interface indicating that committing a given set of changes failed.  */
end_comment

begin_class
specifier|public
class|class
name|CommitFailedException
extends|extends
name|Exception
block|{
comment|/**      * Source name for exceptions thrown by components in the Oak project.      */
specifier|public
specifier|static
specifier|final
name|String
name|OAK
init|=
literal|"Oak"
decl_stmt|;
comment|/**      * Type name for access violation (i.e. permission denied) errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS
init|=
literal|"Access"
decl_stmt|;
comment|/**      * Type name for access control violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL
init|=
literal|"AccessControl"
decl_stmt|;
comment|/**      * Type name for constraint violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|CONSTRAINT
init|=
literal|"Constraint"
decl_stmt|;
comment|/**      * Type name for referencial integrity violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|INTEGRITY
init|=
literal|"Integrity"
decl_stmt|;
comment|/**      * Type name for lock violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|LOCK
init|=
literal|"Lock"
decl_stmt|;
comment|/**      * Type name for name violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"Name"
decl_stmt|;
comment|/**      * Type name for namespace violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE
init|=
literal|"Namespace"
decl_stmt|;
comment|/**      * Type name for node type violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|NODE_TYPE
init|=
literal|"NodeType"
decl_stmt|;
comment|/**      * Type name for state violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"State"
decl_stmt|;
comment|/**      * Type name for version violation errors.      */
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"Version"
decl_stmt|;
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2727602333350620918L
decl_stmt|;
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
specifier|public
name|CommitFailedException
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|type
parameter_list|,
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|format
argument_list|(
literal|"%s%s%04d: %s"
argument_list|,
name|source
argument_list|,
name|type
argument_list|,
name|code
argument_list|,
name|message
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
specifier|public
name|CommitFailedException
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|OAK
argument_list|,
name|type
argument_list|,
name|code
argument_list|,
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CommitFailedException
parameter_list|(
name|String
name|type
parameter_list|,
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|code
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether this exception is of the given type.      *      * @param type type name      * @return {@code true} iff this exception is of the given type      */
specifier|public
name|boolean
name|isOfType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * Checks whether this is an access violation exception.      *      * @return {@code true} iff this is an access violation exception      */
specifier|public
name|boolean
name|isAccessViolation
parameter_list|()
block|{
return|return
name|isOfType
argument_list|(
name|ACCESS
argument_list|)
return|;
block|}
comment|/**      * Checks whether this is an access control violation exception.      *      * @return {@code true} iff this is an access control violation exception      */
specifier|public
name|boolean
name|isAccessControlViolation
parameter_list|()
block|{
return|return
name|isOfType
argument_list|(
name|ACCESS_CONTROL
argument_list|)
return|;
block|}
comment|/**      * Checks whether this is a constraint violation exception.      *      * @return {@code true} iff this is a constraint violation exception      */
specifier|public
name|boolean
name|isConstraintViolation
parameter_list|()
block|{
return|return
name|isOfType
argument_list|(
name|CONSTRAINT
argument_list|)
return|;
block|}
comment|/**      * Returns the name of the source of this exception.      *      * @return source name      */
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
comment|/**      * Return the name of the type of this exception.      *      * @return type name      */
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * Returns the type-specific error code of this exception.      *      * @return error code      */
specifier|public
name|int
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
comment|/**      * Wraps the given {@link CommitFailedException} instance using the      * appropriate {@link javax.jcr.RepositoryException} subclass based on the      * {@link CommitFailedException#getType() type} of the given exception.      *      * @return matching repository exception      */
specifier|public
name|RepositoryException
name|asRepositoryException
parameter_list|()
block|{
return|return
name|asRepositoryException
argument_list|(
name|this
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Wraps the given {@link CommitFailedException} instance using the      * appropriate {@link javax.jcr.RepositoryException} subclass based on the      * {@link CommitFailedException#getType() type} of the given exception.      *      * @param message The exception message.      * @return matching repository exception      */
specifier|public
name|RepositoryException
name|asRepositoryException
parameter_list|(
annotation|@
name|Nonnull
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|isConstraintViolation
argument_list|()
condition|)
block|{
return|return
operator|new
name|ConstraintViolationException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|NAMESPACE
argument_list|)
condition|)
block|{
return|return
operator|new
name|NamespaceException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|NODE_TYPE
argument_list|)
condition|)
block|{
return|return
operator|new
name|NoSuchNodeTypeException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isAccessViolation
argument_list|()
condition|)
block|{
return|return
operator|new
name|AccessDeniedException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isAccessControlViolation
argument_list|()
condition|)
block|{
return|return
operator|new
name|AccessControlException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|INTEGRITY
argument_list|)
condition|)
block|{
return|return
operator|new
name|ReferentialIntegrityException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|STATE
argument_list|)
condition|)
block|{
return|return
operator|new
name|InvalidItemStateException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|VERSION
argument_list|)
condition|)
block|{
return|return
operator|new
name|VersionException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isOfType
argument_list|(
name|LOCK
argument_list|)
condition|)
block|{
return|return
operator|new
name|LockException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|RepositoryException
argument_list|(
name|message
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

