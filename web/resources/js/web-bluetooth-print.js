var data = [];
var device;
var server;
var printService;
var printCharacteristic;
var servicesList = [];
var charsList = [];

//Event Listeners
document.getElementById('printerRegisterForm:printCharacteristic').addEventListener('change',()=>{
    var valueService = document.getElementById('printerRegisterForm:printService').value;
    console.log('entro a evento change');
    if(device != undefined && valueService != undefined){
        document.getElementById('printerRegisterForm:printButton').disabled = false;
        document.getElementById('printerRegisterForm:printButton').classList.remove('ui-state-disabled');
        setHiddenInputs();
    }
});

document.getElementById('confirmPrint').addEventListener('click', ()=>{
    document.getElementById('printerRegisterForm:savePrinterButton').disabled = false;
        document.getElementById('printerRegisterForm:savePrinterButton').classList.remove('ui-state-disabled');
});


async function impresionPrueba(){
    printService = await document.getElementById('printerRegisterForm:printService').value;
    printCharacteristic = await document.getElementById('printerRegisterForm:printCharacteristic').value;
    
    await console.log('entro a impresion');
    
    let encoder = new  EscPosEncoder();
            let result = encoder
                    .newline()
                    .text(`HELLO ${device.name}\n`)
                    .newline()
                    .newline()
                    .encode();
    device.startCharacteristicNotificationsFromPrimaryService(`${printService}`,`${printCharacteristic}`, (notification)=>{
        console.log('notificacion: ',notification);
    });
    device.writeCharacteristicValueFromPrimaryService(`${printService}`,`${printCharacteristic}`,result );
    PF('cd').show();
}


function buscar(){
    navigator
            .bluetooth
            .requestDevice({acceptAllDevices: true})
            .then(async (selectedDevice)=>{
                console.info("entro a buscar");
                device = selectedDevice;
                console.log(device)
                await conectar();
                setPrinterName();
                enableInputs();
            })
            .catch((error)=>{
                disableInputs();
                console.error(error);
            });
}

function conectar(){
   device.gatt.connect()
        .then((ss)=>{
            server = ss;
   });
}

async function getData() {
    let encoder = new EscPosEncoder();

    let result = await encoder
        .initialize()
        .text('hello world' + device.name)
        .newline()
        .encode();
    return result;
}

function enableInputs(){
    document.getElementById('printerRegisterForm:printService').disabled = false;
    document.getElementById('printerRegisterForm:printService').classList.remove('ui-state-disabled');
    document.getElementById('printerRegisterForm:printCharacteristic').disabled = false;
    document.getElementById('printerRegisterForm:printCharacteristic').classList.remove('ui-state-disabled');
}

function disableInputs(){
    document.getElementById('printerRegisterForm:printService').disabled = true;
    document.getElementById('printerRegisterForm:printService').classList.add('ui-state-disabled');
    document.getElementById('printerRegisterForm:printCharacteristic').disabled = true;
    document.getElementById('printerRegisterForm:printCharacteristic').classList.add('ui-state-disabled');
}

function setPrinterName(){
    document.getElementById('printerRegisterForm:printerName').innerHTML = device.name;
    document.getElementById('printerRegisterForm:printerNameHidden').value = device.name;
}

function setHiddenInputs(){
    document.getElementById('printerRegisterForm:printServiceHidden').value = document.getElementById('printerRegisterForm:printService').value;
    document.getElementById('printerRegisterForm:printCharacteristicHidden').value = document.getElementById('printerRegisterForm:printCharacteristic').value;
}

function getData(){
    let arrayDatos = [];
    arrayDatos.push(document.getElementById('').value);
    return arrayDatos;
}

async function encodeAndSend(dataArray){
    let encoder = new  EscPosEncoder();
    let img = new Image();
    img.src = '';
    let result;
    let i =0;
    for(line of dataArray){
        if(i=0){
            result = await encoder
                .newline()
                .text(line)
                .newline()
                .encode();
        }else{
            result = await encoder
                .text(line)
                .newline()
                .qrcode('martin|tepostillo|jimenez')
                .image()
                .encode();
        }
        i++;
        await device.writeCharacteristicValueFromPrimaryService(printService, printCharacteristic, result);
    }
}

function getDataBalanza(){
    let data = [];
    let initialData = document.getElementsByClassName('ui-state-highlight')[0].children;
    for (let i = 0; i < initialData.length; i++){
        data.push(initialData[i].innerText);
    }
    return data;
}

async function searchWithServices(){
    await getServicesAndCharacteristics();
    await navigator.bluetooth.requestDevice({
        filters: [{services: servicesList}]
    })
    .then(async (selectedDevice)=>{
                console.info("entro a buscar");
                device = selectedDevice;
                console.log(device)
                await conectar();
                await checkAndWriteService();
            })
            .catch((error)=>{
                console.error(error);
            });
}

async function checkAndWriteService(){
    console.log('entro a buscacr service');
    for(let s of servicesList){
        for(let c of charsList){
            try{
                await device.writeCharacteristicValueFromPrimaryService(s,c,encodeDataToPrint('\n','left'));
                printService = s;
                printCharacteristic = c;
                await print();
            }catch(e){
                continue;
            }
        }
    }
}

function getServicesAndCharacteristics(){
    let c;
    for(c of document.getElementById('chars').children){
        charsList.push(c.innerText);
    }
    console.log(document.getElementById('services'));
    let s;
    for(s of document.getElementById('services').children){
        servicesList.push(s.innerText);
    }
    console.log(servicesList);
}

async function print(){
    console.log('entro a print')
    let data = await getDataBalanza();
    await console.log(data);
    for(let d=0; d < data.length;d+=2){
            await device.writeCharacteristicValueFromPrimaryService(printService,printCharacteristic,await encodeDataToPrint(data[d], 'left'));
            await device.writeCharacteristicValueFromPrimaryService(printService,printCharacteristic,await encodeDataToPrint(data[d+1], 'right'));
    }
    if(data.length%2 !== 0){
        await device.writeCharacteristicValueFromPrimaryService(printService,printCharacteristic,await encodeDataToPrint('', 'right'));
    }
}

function encodeDataToPrint(data, align){
    let encoder = new  EscPosEncoder();
    let result;
    if(align === 'right'){
        result = encoder
        .codepage('cp1250')
        .text(data)
        .newline()
        .encode();
    }else{
        result = encoder
        .text(data)
        .text('        ')
        .encode();
    }
    return result;
}