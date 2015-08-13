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
name|plugins
operator|.
name|nodetype
operator|.
name|write
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

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
name|annotation
operator|.
name|Nullable
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|namepath
operator|.
name|JcrNameParser
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
name|namepath
operator|.
name|NameMapper
import|;
end_import

begin_comment
comment|/**  * Abstract base class for the template implementations in this package.  * Keeps track of the Oak name of this template and provides utility methods  * for mapping between JCR and Oak names.  */
end_comment

begin_class
specifier|abstract
class|class
name|NamedTemplate
block|{
specifier|private
specifier|final
name|NameMapper
name|mapper
decl_stmt|;
specifier|private
name|String
name|oakName
init|=
literal|null
decl_stmt|;
comment|// not defined by default
specifier|protected
name|NamedTemplate
parameter_list|(
annotation|@
name|Nonnull
name|NameMapper
name|mapper
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
block|}
specifier|protected
name|NamedTemplate
parameter_list|(
annotation|@
name|Nonnull
name|NameMapper
name|mapper
parameter_list|,
annotation|@
name|Nullable
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|jcrName
operator|!=
literal|null
condition|)
block|{
name|setName
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the Oak name of this template, or {@code null} if the name      * has not yet been set.      *      * @return Oak name, or {@code null}      */
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakName
parameter_list|()
block|{
return|return
name|oakName
return|;
block|}
comment|//------------------------------------------------------------< public>--
comment|/**      * Returns the JCR name of this template, or {@code null} if the name      * has not yet been set.      *      * @return JCR name, or {@code null}      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getJcrNameAllowNull
argument_list|(
name|oakName
argument_list|)
return|;
block|}
comment|/**      * Sets the name of this template.      *      * @param jcrName JCR name      * @throws ConstraintViolationException if the name is invalid      */
specifier|public
name|void
name|setName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
name|this
operator|.
name|oakName
operator|=
name|getOakNameOrThrowConstraintViolation
argument_list|(
name|jcrName
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------< name handling utilities>--
comment|/**      * Like {@link NameMapper#getJcrName(String)}, but allows the given Oak      * name to be {@code null}, in which case the return value is also      * {@code null}. Useful for the template implementations where      * {@code null} values are used to indicate undefined attributes.      *      * @param oakName Oak name, or {@code null}      * @return JCR name, or {@code null}      */
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getJcrNameAllowNull
parameter_list|(
annotation|@
name|CheckForNull
name|String
name|oakName
parameter_list|)
block|{
if|if
condition|(
name|oakName
operator|!=
literal|null
condition|)
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|oakName
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Converts the given Oak names to corresponding JCR names. If the given      * array is {@code null} (signifying an undefined set of names), then the      * return value is also {@code null}.      *      * @param oakNames Oak names, or {@code null}      * @return JCR names, or {@code null}      */
annotation|@
name|CheckForNull
specifier|protected
name|String
index|[]
name|getJcrNamesAllowNull
parameter_list|(
annotation|@
name|CheckForNull
name|String
index|[]
name|oakNames
parameter_list|)
block|{
name|String
index|[]
name|jcrNames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|oakNames
operator|!=
literal|null
condition|)
block|{
name|jcrNames
operator|=
operator|new
name|String
index|[
name|oakNames
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|oakNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|jcrNames
index|[
name|i
index|]
operator|=
name|mapper
operator|.
name|getJcrName
argument_list|(
name|oakNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|jcrNames
return|;
block|}
comment|/**      * Converts the given JCR name to the corresponding Oak name. Throws      * a {@link ConstraintViolationException} if the name is {@code null}      * or otherwise invalid.      *      * @param jcrName JCR name      * @return Oak name      * @throws ConstraintViolationException if name is invalid or {@code null}      */
annotation|@
name|Nonnull
specifier|protected
name|String
name|getOakNameOrThrowConstraintViolation
parameter_list|(
annotation|@
name|CheckForNull
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|jcrName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Missing JCR name"
argument_list|)
throw|;
block|}
name|String
name|oakName
init|=
name|mapper
operator|.
name|getOakNameOrNull
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
operator|||
operator|!
name|JcrNameParser
operator|.
name|validate
argument_list|(
name|jcrName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid name: "
operator|+
name|jcrName
argument_list|)
throw|;
block|}
return|return
name|oakName
return|;
block|}
comment|/**      * Like {@link #getOakNameOrThrowConstraintViolation(String)} but allows      * the given JCR name to be {@code null}, in which case the return value      * is also {@code null}.      *      * @param jcrName JCR name, or {@code null}      * @return Oak name, or {@code null}      * @throws ConstraintViolationException if the name is invalid      */
annotation|@
name|CheckForNull
specifier|protected
name|String
name|getOakNameAllowNullOrThrowConstraintViolation
parameter_list|(
annotation|@
name|CheckForNull
name|String
name|jcrName
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|jcrName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|getOakNameOrThrowConstraintViolation
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
block|}
comment|/**      * Converts the given JCR names to corresponding Oak names. Throws      * a {@link ConstraintViolationException} if the given array is      * {@code null} or one of the contained JCR names is {@code null}      * or otherwise invalid.      *      * @param jcrNames JCR names      * @return Oak names      * @throws ConstraintViolationException if names are invalid or {@code null}      */
annotation|@
name|Nonnull
specifier|protected
name|String
index|[]
name|getOakNamesOrThrowConstraintViolation
parameter_list|(
annotation|@
name|CheckForNull
name|String
index|[]
name|jcrNames
parameter_list|)
throws|throws
name|ConstraintViolationException
block|{
if|if
condition|(
name|jcrNames
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|oakNames
init|=
operator|new
name|String
index|[
name|jcrNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jcrNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|oakNames
index|[
name|i
index|]
operator|=
name|getOakNameOrThrowConstraintViolation
argument_list|(
name|jcrNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|oakNames
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Missing JCR names"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

