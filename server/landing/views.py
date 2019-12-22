from django.shortcuts import render

def index(request):
    context = {}
    return render(request, 'landing/indextest.html', context)

# Create your views here.
