interface SirenModel<T> {
    class: string[],
    properties: T,
    links: LinkModel[],
    entities: EntityModel<unknown>[]
    actions: ActionModel[]
}

interface LinkModel{
    rel:string[],
    href: string
}

interface EntityModel<P> {
    properties: P,
    links: LinkModel[],
    rel: string[]
}

interface ActionModel {
    name: string,
    href: string,
    method: string,
    type: string,
    fields: FieldModel[]
}

interface FieldModel{
    class: string[],
    name: string,
    type: string,
    value: string
}

interface SirenWrapperReturn {
    properties: any,    
    class: string[]
    links: {href:string, rel:string}[],
    forms: JSX.Element
}

interface SirenWrapperProps {
    url: string,
    node: <P>( {content}: {content: SirenModel<any>} ) => React.FunctionComponentElement<P>
}
