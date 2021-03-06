package io.manbang.ebatis.core.meta;

import io.manbang.ebatis.core.common.AnnotationUtils;
import io.manbang.ebatis.core.domain.Pageable;
import io.manbang.ebatis.core.generic.GenericType;
import io.manbang.ebatis.core.response.ResponseExtractor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 章多亮
 * @since 2020/5/27 19:05
 */
@ToString(of = "parameter")
class DefaultParameterMeta extends AbstractConditionMeta<Parameter> implements ParameterMeta {
    private final Parameter parameter;
    private final int index;
    private final boolean pageable;
    private final boolean responseExtractor;
    private final boolean basic;
    private final boolean basicArrayOrCollection;
    private final String name;
    private final Annotation requestAnnotation;
    private final Map<Class<? extends Annotation>, Optional<? extends Annotation>> metas = new ConcurrentHashMap<>();

    DefaultParameterMeta(MethodMeta methodMeta, Parameter parameter, int index) {
        super(parameter, parameter.getType(), parameter.getParameterizedType());
        this.parameter = parameter;
        this.index = index;
        this.name = parameter.getName();

        Class<?> type = getActualType(parameter);
        this.basic = MetaUtils.isBasic(type);
        this.pageable = Pageable.class == type;
        this.responseExtractor = ResponseExtractor.class == type;
        this.basicArrayOrCollection = isArrayOrCollection() && basic;
        this.requestAnnotation = methodMeta.getRequestAnnotation();
    }

    private Class<?> getActualType(Parameter parameter) {
        Class<?> type;
        if (isArray()) {
            type = getType().getComponentType();
        } else if (isCollection()) {
            type = GenericType.forMethod((Method) parameter.getDeclaringExecutable()).parameterType(index).resolveGeneric(0);
        } else {
            type = getType();
        }
        return type;
    }

    @Override
    protected String getName(Parameter parameter) {
        String n = super.getName(parameter);
        return StringUtils.isBlank(n) ? parameter.getName() : n;
    }

    @Override
    public Parameter getElement() {
        return parameter;
    }

    @Override
    public Map<Class<? extends Annotation>, List<FieldMeta>> getQueryClauses(Object instance) {
        return ClassMeta.parameter(parameter, instance == null ? null : instance.getClass()).getQueryClauses();
    }

    @Override
    public boolean isPageable() {
        return pageable;
    }

    @Override
    public boolean isResponseExtractor() {
        return responseExtractor;
    }

    @Override
    public int getIndex() {
        return index;
    }


    @Override
    public boolean isBasic() {
        return basic;
    }

    @Override
    public boolean isBasicArrayOrCollection() {
        return basicArrayOrCollection;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<A> findAttributeAnnotation(Class<A> annotationClass) {
        return (Optional<A>) metas.computeIfAbsent(annotationClass, clazz -> AnnotationUtils.findAttributeAnnotation(requestAnnotation, clazz));
    }

    @Override
    public Object getValue(Object[] args) {
        return args[index];
    }
}
